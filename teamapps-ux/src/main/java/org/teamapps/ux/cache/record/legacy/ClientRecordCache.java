/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.cache.record.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.util.StreamUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class ClientRecordCache<RECORD, UIRECORD extends UiIdentifiableClientRecord> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientRecordCache.class);

	private final UiIdentifiableClientRecordFactory<RECORD, UIRECORD> clientRecordFactory;
	private final UiIdentifiableClientRecordPostProcessor<RECORD, UIRECORD> postProcessor;
	private int idCounter;
	private int nextOperationSequenceNumber = 0;
	private int operationInvalidationSequenceNumber = -1;

	private ClientRecordCachePurgeListener purgeListener;
	private BiPredicate<RECORD, Integer> purgeDecider = (record, clientRecordId) -> true;
	private int maxCapacity = Integer.MAX_VALUE;
	
	/**
	 * Always represents the current server-side state.
	 */
	private LinkedHashMap<RECORD, Integer> uiRecordsByRecord = new LinkedHashMap<>();

	/**
	 * Always represents the currently acknowledged client-side state.
	 */
	private LinkedHashMap<Integer, RECORD> recordsByClientId = new LinkedHashMap<>();

	/**
	 * Represents records not yet acknowledged by the client side.
	 * Example: Setting records of a tree, the tree might request child components of an expanded lazy node during its initialization. The corresponding "lazy children" request will come
	 * before the actual acknowledgement of the tree data (with the parent lazy expanded node).
	 */
	private LinkedHashMap<Integer, RECORD> unacknowledgedRecordsByClientId = new LinkedHashMap<>();

	public ClientRecordCache(UiIdentifiableClientRecordFactory<RECORD, UIRECORD> clientRecordFactory, UiIdentifiableClientRecordPostProcessor<RECORD, UIRECORD> postProcessor) {
		this.clientRecordFactory = clientRecordFactory;
		this.postProcessor = postProcessor;
	}

	public ClientRecordCache(UiIdentifiableClientRecordFactory<RECORD, UIRECORD> clientRecordFactory) {
		this(clientRecordFactory, (record, uiRecord, allNewUiRecords) -> {
		});
	}

	public Integer getUiRecordIdOrNull(RECORD record) {
		if (record == null) {
			return null;
		}
		return uiRecordsByRecord.get(record);
	}

	public List<Integer> getUiRecordIds(List<RECORD> records) {
		return records.stream()
				.map(record -> getUiRecordIdOrNull(record))
				.filter(clientId -> clientId != null)
				.collect(Collectors.toList());
	}

	public RECORD getRecordByClientId(int id) {
		RECORD record = recordsByClientId.get(id);
		if (record == null) {
			record = unacknowledgedRecordsByClientId.get(id);
		}
		if (record == null) {
			LOGGER.error("Could not find record for ID from client! Client id: " + id);
		}
		return record;
	}

	public CacheManipulationHandle<Void> clear() {
		uiRecordsByRecord.clear();
		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, null, () -> recordsByClientId.clear());
	}

	/**
	 * Replaces the records, but keeps records inside the cache that are not purged due to the {@link #purgeDecider}'s intervention.
	 */
	public CacheManipulationHandle<List<UIRECORD>> replaceRecords(List<RECORD> newRecords) {
		purgeIfNeeded(maxCapacity);

		LinkedHashMap<RECORD, UIRECORD> uiRecordsByRecord = createUiRecords(newRecords);

		this.uiRecordsByRecord.clear();
		this.uiRecordsByRecord.putAll(uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getId())));

		Map<Integer, RECORD> newRecordsByClientId = uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getValue().getId(), Map.Entry::getKey));
		this.unacknowledgedRecordsByClientId.putAll(newRecordsByClientId);

		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, new ArrayList<>(uiRecordsByRecord.values()),
				() -> {
					this.recordsByClientId.putAll(newRecordsByClientId);
					this.unacknowledgedRecordsByClientId.keySet().removeAll(newRecordsByClientId.keySet());
				});
	}

	public CacheManipulationHandle<List<UIRECORD>> addRecords(List<RECORD> newRecords) {
		purgeIfNeeded(newRecords.size());

		LinkedHashMap<RECORD, UIRECORD> uiRecordsByRecord = createUiRecords(newRecords);
		this.uiRecordsByRecord.putAll(uiRecordsByRecord.entrySet().stream().collect(StreamUtil.toLinkedHashMap(entry -> entry.getKey(), entry -> entry.getValue().getId())));

		Map<Integer, RECORD> newRecordsByClientId = uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getValue().getId(), Map.Entry::getKey));
		this.unacknowledgedRecordsByClientId.putAll(newRecordsByClientId);

		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, new ArrayList<>(uiRecordsByRecord.values()),
				() -> {
					recordsByClientId.putAll(newRecordsByClientId);
					this.unacknowledgedRecordsByClientId.keySet().removeAll(newRecordsByClientId.keySet());
				});
	}

	public CacheManipulationHandle<UIRECORD> addRecord(RECORD record) {
		purgeIfNeeded(1);
		UIRECORD uiRecord = createUiRecord(record);
		postProcessor.postProcess(record, uiRecord, Collections.singletonMap(record, uiRecord));

		uiRecordsByRecord.put(record, uiRecord.getId());
		this.unacknowledgedRecordsByClientId.put(uiRecord.getId(), record);

		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, uiRecord, () -> {
			recordsByClientId.put(uiRecord.getId(), record);
			unacknowledgedRecordsByClientId.remove(uiRecord.getId());
		});
	}

	public CacheManipulationHandle<UIRECORD> addOrUpdateRecord(RECORD record) {
		Integer oldUiRecord = getUiRecordIdOrNull(record);
		UIRECORD newUiRecord = createUiRecord(record);
		postProcessor.postProcess(record, newUiRecord, Collections.singletonMap(record, newUiRecord));
		if (oldUiRecord != null) {
			newUiRecord.setId(oldUiRecord);
		} else {
			purgeIfNeeded(1);
		}

		uiRecordsByRecord.put(record, newUiRecord.getId());
		this.unacknowledgedRecordsByClientId.put(newUiRecord.getId(), record);

		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, newUiRecord, () -> {
			recordsByClientId.put(newUiRecord.getId(), record);
			unacknowledgedRecordsByClientId.remove(newUiRecord.getId());
		});
	}

	public CacheManipulationHandle<Integer> removeRecord(RECORD record) {
		Integer uiRecord = uiRecordsByRecord.remove(record);
		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, uiRecord, () -> recordsByClientId.remove(uiRecord));
	}

	public CacheManipulationHandle<List<Integer>> removeRecords(Collection<RECORD> recordToBeRemoved) {
		List<Integer> removedUiIds = new ArrayList<>();
		uiRecordsByRecord.entrySet().removeIf(entry -> {
			boolean toBeRemoved = recordToBeRemoved.contains(entry.getKey());
			if (toBeRemoved) {
				removedUiIds.add(entry.getValue());
			}
			return toBeRemoved;
		});
		return new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, removedUiIds, () -> recordsByClientId.entrySet().removeIf(entry -> removedUiIds.contains(entry.getKey())));
	}

	public void hardReplaceRecords(Map<RECORD, Integer> newRecords) {
		this.uiRecordsByRecord.clear();
		this.uiRecordsByRecord.putAll(newRecords);
		this.recordsByClientId.clear();
		this.recordsByClientId.putAll(newRecords.entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue(), entry -> entry.getKey())));
		this.unacknowledgedRecordsByClientId.clear();
		this.operationInvalidationSequenceNumber = nextOperationSequenceNumber - 1;
	}

	private void purgeIfNeeded(int numberOfRecordsToBeAdded) {
		int numberOfRecordsToBeRemoved = uiRecordsByRecord.size() + numberOfRecordsToBeAdded - maxCapacity;
		if (numberOfRecordsToBeRemoved > 0) {
			List<Integer> removedClientRecordIds = removeEldestEntries(uiRecordsByRecord, numberOfRecordsToBeRemoved, purgeDecider);

			if (purgeListener != null) {
				purgeListener.handleCacheEntriesPurged(new CacheManipulationHandle<>(this, nextOperationSequenceNumber++, removedClientRecordIds, () -> {
					recordsByClientId.keySet().removeAll(removedClientRecordIds);
				}));
			}
		}

		// make sure we do not get huge memory leaks due to erroneous clients not acknowledging data
		int numberOfRecordsToBeRemovedFromNonAcknowledgedRecordsByClientId = unacknowledgedRecordsByClientId.size() + numberOfRecordsToBeAdded - Math.max(2000, maxCapacity);
		if (numberOfRecordsToBeRemovedFromNonAcknowledgedRecordsByClientId > 0) {
			removeEldestEntries(unacknowledgedRecordsByClientId, numberOfRecordsToBeRemoved, (key, value) -> true);
		}
	}

	private static <K, V> List<V> removeEldestEntries(LinkedHashMap<K, V> map, int numberOfRecordsToBeRemoved, BiPredicate<K, V> purgePredicate) {
		List<V> removedRecords = new ArrayList<>();
		for (Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator(); iterator.hasNext() && removedRecords.size() < numberOfRecordsToBeRemoved; ) {
			Map.Entry<K, V> entry = iterator.next();
			if (purgePredicate.test(entry.getKey(), entry.getValue())) {
				iterator.remove();
				removedRecords.add(entry.getValue());
			}
		}
		return removedRecords;
	}

	private LinkedHashMap<RECORD, UIRECORD> createUiRecords(List<RECORD> newRecords) {
		LinkedHashMap<RECORD, UIRECORD> uiRecordsByRecord = newRecords.stream()
				.collect(StreamUtil.toLinkedHashMap(record -> record, record -> createUiRecord(record)));
		uiRecordsByRecord.forEach((record, uiRecord) -> postProcessor.postProcess(record, uiRecord, uiRecordsByRecord));
		return uiRecordsByRecord;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		purgeIfNeeded(0);
	}

	public ClientRecordCachePurgeListener getPurgeListener() {
		return purgeListener;
	}

	public void setPurgeListener(ClientRecordCachePurgeListener purgeListener) {
		this.purgeListener = purgeListener;
	}

	private UIRECORD createUiRecord(RECORD record) {
		UIRECORD uiRecord = clientRecordFactory.create(record);
		uiRecord.setId(++idCounter);
		return uiRecord;
	}

	public int getOperationInvalidationSequenceNumber() {
		return operationInvalidationSequenceNumber;
	}

	public Map<Integer, RECORD> getAllRecords(boolean includingAcknowledged) {
		var result =  new HashMap<>(recordsByClientId);
		if (includingAcknowledged) {
			result.putAll(unacknowledgedRecordsByClientId);
		}
		return result;
	}

	public <ID> boolean containsExactly(List<RECORD> records, Function<RECORD, ID> identifierExtractor, BiPredicate<RECORD, RECORD> recordsEqual) {
		if (uiRecordsByRecord.size() != records.size()) {
			return false;
		}
		Map<ID, RECORD> cachedRecordsById = uiRecordsByRecord.keySet().stream()
				.collect(Collectors.toMap(identifierExtractor, identity()));
		for (RECORD record : records) {
			ID recordId = identifierExtractor.apply(record);
			RECORD cachedRecord = cachedRecordsById.get(recordId);
			if (cachedRecord == null || !recordsEqual.test(record, cachedRecord)) {
				return false;
			}
		}
		return true;
	}

	public interface UiIdentifiableClientRecordFactory<RECORD, UIRECORD> {
		UIRECORD create(RECORD record);
	}

	public interface UiIdentifiableClientRecordPostProcessor<RECORD, UIRECORD> {
		void postProcess(RECORD record, UIRECORD uiRecord, Map<RECORD, UIRECORD> allNewUiRecords);
	}

	public BiPredicate<RECORD, Integer> getPurgeDecider() {
		return purgeDecider;
	}

	public void setPurgeDecider(BiPredicate<RECORD, Integer> purgeDecider) {
		this.purgeDecider = purgeDecider;
	}
}

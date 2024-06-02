/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
package org.teamapps.projector.clientrecordcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.commons.util.StreamUtil;
import org.teamapps.projector.record.DtoIdentifiableClientRecord;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Caches and maps between client and server records.
 * This cache keeps track of records not yet known to the client and records still known by the client (although obsolete).
 * <p>
 * This is useful for components like comboboxes where the user gets a suggestion by the server in the dropdown. While
 * the user selects the item, the server might decide to send new suggestions. In that case, the selection of the user should still be
 * honored. Otherwise, the user would see the item is selected on the UI, but the server would not remember it,
 * and thereby have to remove it after the fact (bad ux).
 *
 * @param <RECORD>
 * @param <UIRECORD>
 */
public class ClientRecordCache<RECORD, UIRECORD extends DtoIdentifiableClientRecord> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientRecordCache.class);

	private final UiIdentifiableClientRecordFactory<RECORD, UIRECORD> clientRecordFactory;
	private final UiIdentifiableClientRecordPostProcessor<RECORD, UIRECORD> postProcessor;
	private int idCounter;

	private BiPredicate<RECORD, Integer> purgeDecider = (record, clientRecordId) -> true;
	private int maxCapacity = Integer.MAX_VALUE;

	/**
	 * Always represents the current server-side state.
	 */
	private final LinkedHashMap<RECORD, Integer> uiRecordsByRecord = new LinkedHashMap<>();

	/**
	 * Always represents the currently acknowledged client-side state.
	 */
	private final LinkedHashMap<Integer, RECORD> recordsByClientId = new LinkedHashMap<>();

	/**
	 * Represents records not yet acknowledged by the client side.
	 * Example: Setting records of a tree, the tree might request child components of an expanded lazy node during its initialization. The corresponding "lazy children" request will come
	 * before the actual acknowledgement of the tree data (with the parent lazy expanded node).
	 */
	private final LinkedHashMap<Integer, RECORD> unacknowledgedRecordsByClientId = new LinkedHashMap<>();

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
		return new CacheManipulationHandle<>(null, recordsByClientId::clear);
	}

	/**
	 * Replaces the records, but keeps records inside the cache that are not purged due to the {@link #purgeDecider}'s intervention.
	 */
	public CacheManipulationHandle<List<UIRECORD>> replaceRecords(List<RECORD> newRecords) {
		purgeIfNeeded(maxCapacity);

		LinkedHashMap<RECORD, UIRECORD> uiRecordsByRecord = createUiRecords(newRecords);

		this.uiRecordsByRecord.clear();
		this.uiRecordsByRecord.putAll(uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId())));

		Map<Integer, RECORD> newRecordsByClientId = uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getValue().getId(), Map.Entry::getKey));
		this.unacknowledgedRecordsByClientId.putAll(newRecordsByClientId);

		return new CacheManipulationHandle<>(new ArrayList<>(uiRecordsByRecord.values()),
				() -> {
					this.recordsByClientId.putAll(newRecordsByClientId);
					this.unacknowledgedRecordsByClientId.keySet().removeAll(newRecordsByClientId.keySet());
				});
	}

	public CacheManipulationHandle<List<UIRECORD>> addRecords(List<RECORD> newRecords) {
		purgeIfNeeded(newRecords.size());

		LinkedHashMap<RECORD, UIRECORD> uiRecordsByRecord = createUiRecords(newRecords);
		this.uiRecordsByRecord.putAll(uiRecordsByRecord.entrySet().stream().collect(StreamUtil.toLinkedHashMap(Map.Entry::getKey, entry -> entry.getValue().getId())));

		Map<Integer, RECORD> newRecordsByClientId = uiRecordsByRecord.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getValue().getId(), Map.Entry::getKey));
		this.unacknowledgedRecordsByClientId.putAll(newRecordsByClientId);

		return new CacheManipulationHandle<>(new ArrayList<>(uiRecordsByRecord.values()),
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

		return new CacheManipulationHandle<>(uiRecord, () -> {
			recordsByClientId.put(uiRecord.getId(), record);
			unacknowledgedRecordsByClientId.remove(uiRecord.getId());
		});
	}

	public CacheManipulationHandle<Integer> removeRecord(RECORD record) {
		Integer uiRecord = uiRecordsByRecord.remove(record);
		return new CacheManipulationHandle<>(uiRecord, () -> recordsByClientId.remove(uiRecord));
	}

	private void purgeIfNeeded(int numberOfRecordsToBeAdded) {
		int numberOfRecordsToBeRemoved = uiRecordsByRecord.size() + numberOfRecordsToBeAdded - maxCapacity;
		if (numberOfRecordsToBeRemoved > 0) {
			removeEldestEntries(uiRecordsByRecord, numberOfRecordsToBeRemoved, purgeDecider);
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
				.collect(StreamUtil.toLinkedHashMap(record -> record, this::createUiRecord));
		uiRecordsByRecord.forEach((record, uiRecord) -> postProcessor.postProcess(record, uiRecord, uiRecordsByRecord));
		return uiRecordsByRecord;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
		purgeIfNeeded(0);
	}

	private UIRECORD createUiRecord(RECORD record) {
		UIRECORD uiRecord = clientRecordFactory.create(record);
		uiRecord.setId(++idCounter);
		return uiRecord;
	}

	public interface UiIdentifiableClientRecordFactory<RECORD, UIRECORD> {
		UIRECORD create(RECORD record);
	}

	public interface UiIdentifiableClientRecordPostProcessor<RECORD, UIRECORD> {
		void postProcess(RECORD record, UIRECORD uiRecord, Map<RECORD, UIRECORD> allNewUiRecords);
	}

	public void setPurgeDecider(BiPredicate<RECORD, Integer> purgeDecider) {
		this.purgeDecider = purgeDecider;
	}
}

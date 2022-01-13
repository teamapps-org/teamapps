/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.ux.component.media;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioGraph {

	private final byte[] outerMax;
	private final byte[] outerMin;
	private final byte[] innerMax;
	private final byte[] innerMin;
	private final int resolution;
	private final int duration;

	public AudioGraph(int resolution, int duration) {
		this.resolution = resolution;
		this.duration = duration;
		outerMax = new byte[resolution];
		outerMin = new byte[resolution];
		innerMax = new byte[resolution];
		innerMin = new byte[resolution];
	}

	public AudioGraph(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		duration = dis.readInt();
		resolution = dis.readInt();
		outerMax = new byte[resolution];
		outerMin = new byte[resolution];
		innerMax = new byte[resolution];
		innerMin = new byte[resolution];
		for (int i = 0; i < resolution; i++) {
			outerMax[i] = dis.readByte();
			outerMin[i] = dis.readByte();
			innerMax[i] = dis.readByte();
			innerMin[i] = dis.readByte();
		}
	}

	public byte[] save() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(duration);
		dos.writeInt(resolution);
		for (int i = 0; i < resolution; i++) {
			dos.writeByte(outerMax[i]);
			dos.writeByte(outerMin[i]);
			dos.writeByte(innerMax[i]);
			dos.writeByte(innerMin[i]);
		}
		return bos.toByteArray();
	}


	private byte[] readArray(DataInputStream dis) throws IOException {
		int len = dis.readInt();
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			bytes[i] = dis.readByte();
		}
		return bytes;
	}

	public MediaTrackData createTrackData() {
		MediaTrackData data = new MediaTrackData();
		data.setTrackCount(1);
		double timeFactor = (duration * 1d) / resolution;
		for (int i = 0; i < resolution; i++) {
			data.addData((long) (i * timeFactor), getGraphData(i));
		}
		return data;
	}

	private int[] getGraphData(int pos) {
		int[] value = new int[4];
		value[0] = getConvertedValue(pos, outerMax);
		value[1] = getConvertedValue(pos, outerMin);
		value[2] = getConvertedValue(pos, innerMax);
		value[3] = getConvertedValue(pos, innerMin);
		return value;
	}

	private int getConvertedValue(int pos, byte[] data) {
		int value = data[pos];
		return (int) (value / 1.28f);
	}

	public byte[] getOuterMax() {
		return outerMax;
	}

	public byte[] getOuterMin() {
		return outerMin;
	}

	public byte[] getInnerMax() {
		return innerMax;
	}

	public byte[] getInnerMin() {
		return innerMin;
	}

	public int getResolution() {
		return resolution;
	}

	public int getDuration() {
		return duration;
	}

	public static AudioGraph createGraph(File pcmFile) throws IOException {
        return createGraph(pcmFile, 400, (int) (pcmFile.length() / 16), 8000);
    }

	public static AudioGraph createGraph(File pcmFile, int resolution) throws IOException {
		return createGraph(pcmFile, resolution, (int) (pcmFile.length() / 16), 8000);
	}

	public static AudioGraph createGraph(File pcmFile, int resolution, int durationMs, int sampleRate) throws IOException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(pcmFile));
			return createGraph(in, resolution, durationMs, sampleRate);
		} finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	public static AudioGraph createGraph(InputStream pcmIn, int resolution, int durationMs, int sampleRate) throws IOException {
		AudioGraph graph = new AudioGraph(resolution, durationMs);
		DataInputStream in = new DataInputStream(new BufferedInputStream(pcmIn));
		
		final int samplesPerPoint = (int) ((long)durationMs * sampleRate / 1000 / resolution);
		final int msPerPoint = durationMs / resolution;
		
		if (samplesPerPoint == 0 || msPerPoint == 0) return graph;
		
		byte[] buf = new byte[4 * samplesPerPoint];
		ByteBuffer sampleBuf = ByteBuffer.wrap(buf);
		sampleBuf.order(ByteOrder.LITTLE_ENDIAN);
		
		int pos = 0;
		while (pos < resolution) {
			Arrays.fill(buf, (byte)0);
			int samples = (int) ((long)durationMs * (pos + 1) * sampleRate / 1000 / resolution - (long)durationMs * pos * sampleRate / 1000 / resolution);
			if (samples == 0) break;
			final int bytes = samples * 2;
			int read = in.read(buf, 0, bytes);
			if (read < 0) break;
			if (read < bytes) System.err.println("read " + read + " of " + bytes);
			
			short max;
			short min;
			short avg;
			short rms;
			
			long sum = 0;
			
			max = Short.MIN_VALUE;
			min = Short.MAX_VALUE;

			sampleBuf.position(0);
			for (int j = 0; j < samples; ++j) {
				short val = sampleBuf.getShort();
				
				if (val > max) max = val;
				if (val < min) min = val;
				sum += val;
			}
			
			avg = (short) (sum / samples);

			long squares = 0;
			sampleBuf.position(0);
			for (int j = 0; j < samples; j++) {
				int deviation = sampleBuf.getShort() - avg;
				squares += deviation * deviation;
			}
			
			rms = (short) Math.sqrt(squares / samples);

			graph.outerMax[pos] = toByte(max);
			graph.outerMin[pos] = toByte(min);
			graph.innerMax[pos] = toByte((short) (avg + rms));
			graph.innerMin[pos] = toByte((short) (avg - rms));
			
			++pos;
		}
		
		return graph;
	}

	private static byte toByte(short max) {
		byte b = (byte) (max / 256);
		if (b == Byte.MIN_VALUE) ++b;
		return b;
	}
}

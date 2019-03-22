/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.servlet.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.zip.InflaterOutputStream;

public class ZlibUtilTest {

    @Test
    public void testDefalteAndInflate() throws Exception {
        String inputString = "hallo";
        byte[] deflated = ZlibUtil.deflateString(inputString);
        String inflated = ZlibUtil.inflateToString(deflated);
        Assert.assertEquals(inputString, inflated);
    }

    @Test
    public void testName() throws Exception {
        byte[] bytes = {120, (byte)156, (byte)203, 72, (byte)204, (byte)201, (byte)201, 7, 0, 6, 28, 2, 17};
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(bytes.length * 5);
            try (InflaterOutputStream deflateStream = new InflaterOutputStream(byteOutStream)) {
                deflateStream.write(bytes);
            }
            byte[] result = byteOutStream.toByteArray();
            Assert.assertEquals("hallo", new String(result, "UTF8"));

    }

    @Test
    public void testName2() throws Exception {
        byte[] bytes = {
                120, (byte)156, 69, (byte)142, (byte)209, 74, (byte)195, 64, 16, 69, (byte)255, 101, (byte)158, (byte)179, (byte)184, (byte)137, (byte)217, (byte)152, (byte)238, (byte)163, 90, 49, (byte)160, (byte)173, 96, 69, 80, 68, (byte)146, (byte)221, 27, 51, 88, 55, 101, 51, 81, (byte)138, (byte)248, (byte)239, 46, 98, (byte)233, (byte)227, (byte)156, 123, (byte)238, 112, (byte)191, (byte)233, 85, (byte)246, 59, (byte)144, (byte)165, (byte)139, (byte)155, 102, (byte)185, (byte)218, (byte)168, 102, 117, (byte)181, (byte)166, (byte)140, 62, 25, 95, 119, 99, (byte)148, 71, (byte)246, 50, (byte)144, (byte)173, 76, 125, 100, (byte)215, (byte)224, (byte)183, 65, (byte)200, 46, 76, (byte)149, (byte)209, (byte)228, 34, 16, (byte)254, (byte)181, (byte)188, (byte)170, (byte)245, 1, 29, (byte)172, 92, (byte)155, (byte)196, (byte)134, 116, 92, 34, 76, 44, (byte)251, (byte)251, (byte)191, (byte)152, (byte)172, (byte)196, 25, 25, 9, 127, (byte)224, 105, 12, 88, (byte)247, (byte)253, 4, (byte)185, (byte)229, 48, 11, 38, (byte)178, 42, 47, (byte)244, 49, 108, (byte)218, (byte)208, (byte)166, (byte)137, (byte)203, 57, (byte)142, 59, (byte)156, (byte)156, 35, 110, 57, (byte)164, (byte)145, 110, (byte)203, 8, (byte)178, 25, (byte)223, (byte)211, 99, (byte)178, (byte)207, 47, (byte)169, (byte)208, 118, 15, 51, (byte)251, (byte)228, 2, (byte)168, (byte)141, (byte)214, (byte)189, (byte)242, 48, 80, 101, (byte)225, 10, (byte)213, 57, 15, (byte)229, (byte)171, (byte)190, 116, 56, (byte)235, 78, 81, 46, (byte)232, (byte)231, 23, (byte)252, 88, 83, (byte)143
        };
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(bytes.length * 5);
        try (InflaterOutputStream deflateStream = new InflaterOutputStream(byteOutStream)) {
            deflateStream.write(bytes);
        }
        byte[] result = byteOutStream.toByteArray();
        Assert.assertEquals("{\"_type\":\"CLIENT-INFO\",\"viewPortWidth\":658,\"viewPortHeight\":956,\"screenWidth\":1680,\"screenHeight\":1050," +
                "\"highDensityScreen\":true,\"timeZoneOffsetMinutes\":-120,\"timeZoneIana\":\"Europe/Berlin\",\"clientTokens\":[]," +
                "\"tabUuid\":\"eee8500f-de5e-42c2-bcde-d6f4ce7b3e49\"}", new String(result, "UTF8"));

    }
}

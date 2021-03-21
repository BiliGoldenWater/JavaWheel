package com.company.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NTPTime {
    private int timeout = 3000;
    private int port = 123;
    private String address = "ntp1.aliyun.com";
    private long timestamp;

    public NTPTime() throws IOException {
        this.timestamp = getTimeStamp();
    }

    public NTPTime(String address) throws IOException {
        this.address = address;
        this.timestamp = getTimeStamp();
    }

    public long getTimestampMilliseconds(){
        return this.timestamp;
    }

    public void updateTime() throws IOException {
        this.timestamp = getTimeStamp();
    }

    public void changeAddress(String address){
        this.address = address;
    }

    public void changePort(int port){
        this.port = port;
    }

    public void setTimeout(int timeout){
        this.timeout = timeout;
    }

    private long getTimeStamp() throws IOException {
        InetAddress ipv4Address = InetAddress.getByName(this.address);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(this.timeout);

            // 请求ntp服务器
            byte[] data = new NTPMessage().toByteArray();

            DatagramPacket outgoing = new DatagramPacket(data, data.length, ipv4Address, this.port);
            socket.send(outgoing);

            // 接收ntp服务器响应
            DatagramPacket incoming = new DatagramPacket(data, data.length);
            socket.receive(incoming);

            // Validate NTP Response
            // IOException thrown if packet does not decode as expected.
            NTPMessage msg = new NTPMessage(incoming.getData());

            // timestamp is relative to 1900, utc is used by Java and is relative
            // to 1970
            double utc = msg.transmitTimestamp - (2208988800.0);

            // milliseconds
            return (long) (utc * 1000.0);
        }
    }

    public static class NTPMessage {
        public byte leapIndicator = 0;
        public byte version = 3;
        public byte mode = 0;
        public short stratum = 0;
        public byte pollInterval = 0;
        public byte precision = 0;
        public double rootDelay = 0;
        public double rootDispersion = 0;
        public byte[] referenceIdentifier = { 0, 0, 0, 0 };
        public double referenceTimestamp = 0;
        public double originateTimestamp = 0;
        public double receiveTimestamp = 0;
        public double transmitTimestamp;

        public NTPMessage(byte[] array) {
            leapIndicator = (byte) ((array[0] >> 6) & 0x3);
            version = (byte) ((array[0] >> 3) & 0x7);
            mode = (byte) (array[0] & 0x7);
            stratum = unsignedByteToShort(array[1]);
            pollInterval = array[2];
            precision = array[3];

            rootDelay = (array[4] * 256.0) + unsignedByteToShort(array[5]) + (unsignedByteToShort(array[6]) / 256.0) + (unsignedByteToShort(array[7]) / 65536.0);

            rootDispersion = (unsignedByteToShort(array[8]) * 256.0) + unsignedByteToShort(array[9]) + (unsignedByteToShort(array[10]) / 256.0) + (unsignedByteToShort(array[11]) / 65536.0);

            referenceIdentifier[0] = array[12];
            referenceIdentifier[1] = array[13];
            referenceIdentifier[2] = array[14];
            referenceIdentifier[3] = array[15];

            referenceTimestamp = decodeTimestamp(array, 16);
            originateTimestamp = decodeTimestamp(array, 24);
            receiveTimestamp = decodeTimestamp(array, 32);
            transmitTimestamp = decodeTimestamp(array, 40);
        }

        public NTPMessage(byte leapIndicator, byte version, byte mode, short stratum, byte pollInterval, byte precision, double rootDelay, double rootDispersion, byte[] referenceIdentifier, double referenceTimestamp, double originateTimestamp, double receiveTimestamp, double transmitTimestamp) {
            this.leapIndicator = leapIndicator;
            this.version = version;
            this.mode = mode;
            this.stratum = stratum;
            this.pollInterval = pollInterval;
            this.precision = precision;
            this.rootDelay = rootDelay;
            this.rootDispersion = rootDispersion;
            this.referenceIdentifier = referenceIdentifier;
            this.referenceTimestamp = referenceTimestamp;
            this.originateTimestamp = originateTimestamp;
            this.receiveTimestamp = receiveTimestamp;
            this.transmitTimestamp = transmitTimestamp;
        }

        public NTPMessage() {
            this.mode = 3;
            this.transmitTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;
        }

        public byte[] toByteArray() {
            byte[] p = new byte[48];

            p[0] = (byte) (leapIndicator << 6 | version << 3 | mode);
            p[1] = (byte) stratum;
            p[2] = pollInterval;
            p[3] = precision;

            int l = (int) (rootDelay * 65536.0);
            p[4] = (byte) ((l >> 24) & 0xFF);
            p[5] = (byte) ((l >> 16) & 0xFF);
            p[6] = (byte) ((l >> 8) & 0xFF);
            p[7] = (byte) (l & 0xFF);

            long ul = (long) (rootDispersion * 65536.0);
            p[8] = (byte) ((ul >> 24) & 0xFF);
            p[9] = (byte) ((ul >> 16) & 0xFF);
            p[10] = (byte) ((ul >> 8) & 0xFF);
            p[11] = (byte) (ul & 0xFF);

            p[12] = referenceIdentifier[0];
            p[13] = referenceIdentifier[1];
            p[14] = referenceIdentifier[2];
            p[15] = referenceIdentifier[3];

            encodeTimestamp(p, 16, referenceTimestamp);
            encodeTimestamp(p, 24, originateTimestamp);
            encodeTimestamp(p, 32, receiveTimestamp);
            encodeTimestamp(p, 40, transmitTimestamp);

            return p;
        }

        public String toString() {
            String precisionStr = new DecimalFormat("0.#E0").format(Math.pow(2, precision));
            return "Leap indicator: " + leapIndicator + " \n"
                    + "Version: " + version + " \n"
                    + "Mode: " + mode + " \n"
                    + "Stratum: " + stratum + " \n"
                    + "Poll: " + pollInterval + " \n"
                    + "Precision: " + precision + " (" + precisionStr + " seconds) \n"
                    + "Root delay: " + new DecimalFormat("0.00").format(rootDelay * 1000) + " ms \n"
                    + "Root dispersion: " + new DecimalFormat("0.00").format(rootDispersion * 1000) + " ms \n"
                    + "Reference identifier: " + referenceIdentifierToString(referenceIdentifier, stratum, version) + " \n"
                    + "Reference timestamp: " + timestampToString(referenceTimestamp) + " \n"
                    + "Originate timestamp: " + timestampToString(originateTimestamp) + " \n"
                    + "Receive timestamp:   " + timestampToString(receiveTimestamp) + " \n"
                    + "Transmit timestamp: " + timestampToString(transmitTimestamp);
        }

        public static short unsignedByteToShort(byte b) {
            if ((b & 0x80) == 0x80)
                return (short) (128 + (b & 0x7f));
            else
                return b;
        }

        public static double decodeTimestamp(byte[] array, int pointer) {
            double r = 0.0;

            for (int i = 0; i < 8; i++) {
                r += unsignedByteToShort(array[pointer + i]) * Math.pow(2, (3 - i) * 8);
            }

            return r;
        }

        public static void encodeTimestamp(byte[] array, int pointer, double timestamp) {
            for (int i = 0; i < 8; i++) {
                // 2^24, 2^16, 2^8, .. 2^-32
                double base = Math.pow(2, (3 - i) * 8);

                array[pointer + i] = (byte) (timestamp / base);

                timestamp = timestamp - (unsignedByteToShort(array[pointer + i]) * base);
            }

            array[7] = (byte) (Math.random() * 255.0);
        }

        public static String timestampToString(double timestamp) {
            if (timestamp == 0)
                return "0";

            double utc = timestamp - (2208988800.0);

            long ms = (long) (utc * 1000.0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            String date = simpleDateFormat.format(new Date(ms));

            double fraction = timestamp - ((long) timestamp);
            String fractionSting = new DecimalFormat(".000000").format(fraction);

            return date + fractionSting;
        }

        public static String referenceIdentifierToString(byte[] ref, short stratum, byte version) {
            if (stratum == 0 || stratum == 1) {
                return new String(ref);
            }

            else if (version == 3) {
                return unsignedByteToShort(ref[0]) + "." + unsignedByteToShort(ref[1]) + "." + unsignedByteToShort(ref[2]) + "." + unsignedByteToShort(ref[3]);
            }

            else if (version == 4) {
                return "" + ((unsignedByteToShort(ref[0]) / 256.0) + (unsignedByteToShort(ref[1]) / 65536.0) + (unsignedByteToShort(ref[2]) / 16777216.0) + (unsignedByteToShort(ref[3]) / 4294967296.0));
            }

            return "";
        }
    }
}

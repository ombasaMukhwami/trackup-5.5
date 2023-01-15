package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.BaseProtocolDecoder;
import org.traccar.session.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.Helper;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class TntTextProtocolDecoder extends BaseProtocolDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(TntTextProtocolDecoder.class);
    public TntTextProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    //30/07/19,00:53:07,860675049563586,GV9,KCA379D,0,36.820081,E,1.267554,S,0,1
    private static final Pattern PATTERN = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")     // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+.d+),")            // longitute
            .expression("([EW]),")         //direction
            .number("(d+.d+),")            // latitute
            .expression("([NS]),")         //Direction
            .number("([01]),")             //ignition
            .number("([01])")              //Power status
            .any()
            .compile();

    //OK,08:55:30,860298040114224,controlteach,KCK 539T,0,1.302800,S,36.849383,E,0,1,0,0,0#
    private static final Pattern PATTERN_OK = new PatternBuilder()
            .expression("(.+),")     //date (ddmmyyyy)
            .expression("(.+),")     //time (hhmmss)
            .expression("(d+),")            //imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               //speed
            .number("(d+.d+),")            //latitute
            .expression("([NS]),")         //direction
            .number("(d+.d+),")            //Longitute
            .expression("([EW]),")         //Direction
            .number("(d+),")               //power source
            .number("(d+),")               //speed status
            .number("(d+),")               //heading
            .number("(d+),")               //gps fix
            .number("(d+)")                //ignition status
            .any()
            .compile();

    //08/27/19,02:15:00,60298040118944,GV4,KCJ 948U,0,1.217283,S,36.883700,E,0,1,0,0,0#
    //08/28/19,12:38:20,08994037848624,GV4,000000000,0,1.401850,S,36.724500,E,1,1,0,0,0#
    private static final Pattern PATTERN_ORD = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")     //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     //time (hhmmss)
            .expression("(d+),")            //imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               //speed
            .number("(d+.d+),")            //latitute
            .expression("([NS]),")         //direction
            .number("(d+.d+),")            //Longitute
            .expression("([EW]),")         //Direction
            .number("(d+),")               //power source
            .number("(d+),")               //speed status
            .number("(d+),")               //heading
            .number("(d+),")               //gps fix
            .number("(d+)")                //ignition status
            .any()
            .compile();

    private static final Pattern PATTERN_KARIBIA = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")         //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")         //time (hhmmss)
            .number("(d+),")                   //imei
            .expression("(.+),")               //VendorId
            .expression("(.+),")               //Vehicle registration
            .number("(d+),")                   //speed
            .number("(d+.d+),")                //Longitute
            .expression("([NS]),")             //direction
            .number("(d+.d+),")                //Latitute
            .expression("([EW]),")             //Direction
            .number("([01]),")                 //Power Connection(1 diconnected, 0 connected)
            .number("([01]),")                 //Speed Signal (0 disconnected
            .number("(d+),")                   //Course
            .number("([01]),")                 //Ignition status 1-on, 0 off
            .number("([01]),")                  //Antennar disconnection 1 disconnected
            .number("([01])")                  //overspeed
            .any()
            .compile();


    private static final Pattern PATTERN_KARIBIA_WITHOUT_IMEI = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")         //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")         //time (hhmmss)
            .expression("(.+),")               //imei
            .expression("(.+),")               //VendorId
            .expression("(.+),")               //Vehicle registration
            .number("(d+),")                   //speed
            .number("(d+.d+),")                //Longitute
            .expression("([NS]),")             //direction
            .number("(d+.d+),")                //Latitute
            .expression("([EW]),")             //Direction
            .number("([01]),")                 //Power Connection(1 diconnected, 0 connected)
            .number("([01]),")                 //Speed Signal (0 disconnected
            .number("(d+),")                   //Course
            .number("([01]),")                 //Ignition status 1-on, 0 off
            .number("([01])")                  //Antennar disconnection 1 disconnected
            .any()
            .compile();

    private static final Pattern PATTERN_YEAR = new PatternBuilder()
            .number("(dddd)-(dd)-(dd),")   //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     //time (hhmmss)
            .number("(d+),")               //imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               //speed
            .number("(d+),")               //course
            .number("(d+),")               //2
            .number("(d+),")               //0
            .number("(d+.d+),")            //longitute
            .expression("([EW])")          //direction
            .expression("(.+),")           //(+-)
            .number("(d+.d+),")            // latitute
            .expression("([NS])")          //Direction
            .expression("(.+),")           //(+-)
            .number("([01]),")             //ignition
            .number("([01]),")             //Power status
            .number("([01])")              //Antenna status
            .any()
            .compile();

    private static final Pattern PATTERN_EVENT = new PatternBuilder()
            .number("(dddd)-(dd)-(dd),")   // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.*),")           //VendorId
            .expression("(.*),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+),")               // course
            .number("(d+),")               // 2
            .number("(d+),")               // 0
            .number("(d+.d+),")            // longitute
            .expression("([EW])")          //direction
            .expression("(.+),")           //(+-)
            .number("(d+.d+),")            // latitute
            .expression("([NS])")          //Direction
            .expression("(.+),")           //(+-)
            .number("([01]),")             //ignition
            .number("([01]),")             //Power status
            .number("([01]),")             //Antenna status
            .expression("(.{3})")
            .any()
            .compile();

    private static final Pattern PATTERN_CPIN = new PatternBuilder()
            .number("(dddd)-(dd)-(dd),")   // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .expression("(.+),")           // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+),")               // course
            .number("(d+),")               // 2
            .number("(d+),")               // 0
            .number("(d+.d+),")            // longitute
            .expression("([EW])")          //direction
            .expression("(.+),")           //(+-)
            .number("(d+.d+),")            // latitute
            .expression("([NS])")          //Direction
            .expression("(.+),")           //(+-)
            .number("([01]),")             //ignition
            .number("([01]),")             //Power status
            .number("([01]),")             //Antenna status
            .expression("(.{3})")
            .any()
            .compile();

    private static final Pattern PATTERN_DALCOM = new PatternBuilder()
            .text("*")
            .number("(dd)/(dd)/(dddd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+.d+),")                  // speed
            .number("(d+.d+),")                  // latitude
            .expression("([NS]),")               //direction
            .number("(d+.d+),")                  // longitude
            .expression("([EW]),")               //Direction
            .number("([01]),")                   //ignition
            .number("([01]),")                   //signal wire
            .number("([01]),")                   //connector
            .number("([01]),")                   //enclosure
            .expression("(.+),")                 // speed source
            .number("(d+.d+),")                  // voltage
            .number("([01]),")                   //Callibration
            .number("(d+),")                     // SpeedLimit
            .number("(d+),")                     // GpsSpeed
            .number("([01])")                    // GpsFix
            .any()
            .compile();

    private static final Pattern PATTERN_NEXT = new PatternBuilder()
            .text("*")
            .number("(dd)/(dd)/(dddd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+.d+),")                  // speed
            .number("(d+),")                    // latitude
            .expression("(.+),")                //direction
            .number("(d+),")                    // longitude
            .expression("(.+),")                //Direction
            .number("([01]),")                  //ignition
            .number("([01]),")                  //signal wire
            .number("([01]),")                  //connector
            .number("([01]),")                  //enclosure
            .expression("(.+),")                // speed source
            .number("(d+.d+),")                 // voltage
            .number("([01]),")                  //Callibration
            .number("(d+),")                    // SpeedLimit
            .number("(d+),")                    // GpsSpeed
            .number("([01])")                   // GpsFix
            .any()
            .compile();

    private static final Pattern PATTERN_TMC = new PatternBuilder()
            .number("(dd)/(dd)/(dddd),")         //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")         //time (hhmmss)
            .expression("(.+),")               //imei
            .expression("(.+),")               //VendorId
            .expression("(.+),")               //Vehicle registration
            .number("(d+),")                   //speed
            .number("(-?d+.d+),")                //Longitute
            .expression("([NS]),")             //direction
            .number("(-?d+.d+),")                //Latitute
            .expression("([EW]),")             //Direction
            .number("([01]),")                 //Power Connection(1 diconnected, 0 connected)
            .number("([01]),")                 //Speed Signal (0 disconnected
            .number("([01])")                  //Antennar disconnection 1 disconnected
            .any()
            .compile();

    private static final Pattern PATTERN_TMC_INVALID_DATA = new PatternBuilder()
            .number("(dd)/(dd)/(dddd),")         //date (ddmmyyyy)
            .number("(dd):(dd):(dd),")         //time (hhmmss)
            .expression("(.+),")               //imei
            .expression("(.+),")               //VendorId
            .expression("(.*)")                //any
            .number("([01]),")                 //Power Connection(1 diconnected, 0 connected)
            .number("([01]),")                 //Speed Signal (0 disconnected
            .number("(d+),")                   //Course
            .number("([01]),")                 //Ignition status 1-on, 0 off
            .number("([01]),")                  //Antennar disconnection 1 disconnected
            .number("([01])")                  //overspeed
            .any()
            .compile();
    private static final Pattern PATTERN_DATETIME = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")   // date (ddmmyyyy)
            .number("(dd):(dd):(dd)")     // time (hhmmss)
            .any()
            .compile();


    private String decodeAlarm(String alarm) {

        switch (alarm) {
            case "A01":
            case "A1":
                return Position.ALARM_POWER_ON;
            case "A02":
            case "A2":
                return Position.ALARM_POWER_OFF;
            case "A03":
            case "A3":
                return Position.ALARM_OVERSPEED;
            case "A04":
            case "A05":
            case "A06":
            case "A07":
            case "A7":
                return Position.ALARM_POWER_CUT;
            case "A08":
            case "A8":
                return Position.ALARM_POWER_RESTORED;
            case "A09":
            case "A10":
            case "A11":
                return Position.ALARM_GPS_ANTENNA_CUT;
            case "A12":
                return Position.ALARM_GPS_RECONNECTED;
            default:
                return  null;
        }
    }

    private Position decodeDalcom(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_DALCOM, sentence);
        if (!parser.matches()) {
            parser = new Parser(PATTERN_NEXT, sentence);
            if (!parser.matches()) {
                return decodeOrdinary(position, channel, remoteAddress, sentence);
            }
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        String latDirection, lonDirection;
        double a, b, c, d;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        String lat = parser.next();
        double iLat = 0;
        double lastLat = 0;
        if (lat.length() > 4) {
            a = Double.parseDouble(lat.substring(0, 2));
            b = (Double.parseDouble(lat.substring(2)) / 60.0);
            iLat  =  a +  b;
            lastLat = iLat;
        }

        latDirection  =  parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDirection);
        String lon = parser.next();
        double iLon  = 0;

        double lastLon = 0;
        if (lon.length() > 4) {
            c = Double.parseDouble(lon.substring(0, 3));
            d = (Double.parseDouble(lon.substring(3)) / 60.0);
            iLon  =  c + d;
            lastLon = iLon;
        }
        lonDirection = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDirection);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_CONNECTOR, parser.next());
        position.set(Position.KEY_ENCLOSURE, parser.next());
        position.set(Position.KEY_SPEED_SOURCE, parser.next());
        position.set(Position.KEY_POWER, parser.nextDouble());
        position.set(Position.KEY_CALIBRATION, parser.next());
        position.set(Position.KEY_SPEED_LIMIT, parser.next());
        position.set(Position.KEY_GPS_SPEED, parser.next());
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0);
        if (latDirection.equalsIgnoreCase("S")) {
            iLat  = -1 * iLat;
        }
        if (lonDirection.equalsIgnoreCase("W")) {
            iLon  = -1 * iLon;
        }

        if (lastLat == 0 && lastLon == 0) {
            getLastLocation(position, position.getDeviceTime());
        } else {
            position.setLatitude(iLat);
            position.setLongitude(iLon);
        }



        return position;
    }

    private Position decodeKaribiaGeneral(Parser parser, Position position, DeviceSession deviceSession) {
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lat = parser.nextDouble();
        latDir = parser.next();
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0); // 1- Disconnected
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0); // 1- Disconnected
        position.setCourse(parser.nextInt());
        position.set(Position.KEY_IGNITION, parser.nextInt() == 1); // 1- On
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0); // 1- Disconnected
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);

        return position;
    }

    public Position decodeKaribiawithoutimei(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_KARIBIA_WITHOUT_IMEI, sentence);
        if (!parser.matches()) {
            return decodeDalcom(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        String imei = Helper.removeZero(parser.next()).replaceAll("\\s", "").replaceAll("\\u0000", "");
        DeviceSession deviceSession;
        if (StringUtils.isNumber(imei)) {
            deviceSession = getDeviceSession(channel, remoteAddress, imei);
        } else {
            deviceSession = getDeviceSession(channel, remoteAddress);
        }

        if (deviceSession == null) {
            return null;
        }

        return decodeKaribiaGeneral(parser, position, deviceSession);
    }

    public Position decodeKaribia(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_KARIBIA, sentence);
        if (!parser.matches()) {
            return decodeKaribiawithoutimei(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        decodeKaribiaGeneral(parser, position, deviceSession);
        int overSpeed = parser.nextInt();
        if (overSpeed == 1) {
            position.set(Position.KEY_ALARM, Position.ALARM_OVERSPEED);
        }
        return position;
    }


    public Position decodeCpin(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.setCourse(parser.nextInt());
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0);
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        lon = parser.nextDouble();
        lonDir = parser.next();
        parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_IGNITION, parser.nextInt() == 1);
        position.set(Position.KEY_ALARM, decodeAlarm(parser.next()));
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    private Position decodeEvent(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.set(Position.KEY_ODOMETER, parser.nextInt() * 1000);
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0);
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        lon = parser.nextDouble();
        lonDir = parser.next();
        parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_IGNITION, parser.nextInt() == 0);
        position.set(Position.KEY_ALARM, decodeAlarm(parser.next()));
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    private Position decodeYear(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_YEAR, sentence);
        if (!parser.matches()) {
            return decodeMessage(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.set(Position.KEY_ODOMETER, parser.nextInt() * 1000);
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0);
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        lon = parser.nextDouble();
        lonDir = parser.next();
        parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_IGNITION, parser.nextInt() == 0); // 0 ignition ON 1- Ignition OFF
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Position decodeOrdinary(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_ORD, sentence);
        if (!parser.matches()) {
            return decodeOk(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        String imei = parser.next().replace("[^\\d]", "").replaceAll("\\u0000", "");
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        parser.next();
        parser.next();
        position.set(Position.KEY_IGNITION, parser.nextInt() == 0);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Position decodeOk(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {
        Parser parser = new Parser(PATTERN_OK, sentence);
        if (!parser.matches()) {
            return decodeTmc(position, channel, remoteAddress, sentence);
        }
        String ok = parser.next();
        String time = parser.next();
        String date = java.time.LocalDate.now().toString();
        String curDate = String.format("%s %s", date, time);
        Date gpsDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(curDate);
        position.setTime(gpsDate);
        String imei = parser.next();
        position.set("imei", imei);
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        parser.next();
        parser.next();
        position.set(Position.KEY_IGNITION, parser.nextInt() == 1);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Position decodeMessage(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {

        Parser parser = new Parser(PATTERN, sentence);
        if (!parser.matches()) {
            return decodeKaribia(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);

        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Position decodeTmc(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {

        Parser parser = new Parser(PATTERN_TMC, sentence);
        if (!parser.matches()) {
            return decodeTmcWithError(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0);
        position.set(Position.ALARM_GPS_RECONNECTED, parser.nextInt() == 0);
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Position decodeTmcWithError(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {

        Parser parser = new Parser(PATTERN_TMC_INVALID_DATA, sentence);
        if (!parser.matches()) {
            return decodeDateTimeImei(position, channel, remoteAddress, sentence);
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        getLastLocation(position, position.getDeviceTime());
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        parser.next();
        position.set(Position.KEY_POWER_WIRE, parser.nextInt() == 0); // 1- Disconnected
        position.set(Position.KEY_SIGNAL_WIRE, parser.nextInt() == 0); // 1- Disconnected
        position.setCourse(parser.nextInt());
        position.set(Position.KEY_IGNITION, parser.nextInt() == 1); // 1- On
        position.set(Position.KEY_ANTENNA_CONNECTED, parser.nextInt() == 0); // 1- Disconnected

        return position;
    }

    public Position decodeDateTimeImei(Position position, Channel channel, SocketAddress remoteAddress, String sentence) throws Exception {

        Parser parser = new Parser(PATTERN_DATETIME, sentence);
        if (!parser.matches()) {
            return null;
        }
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());
        getLastLocation(position, position.getDeviceTime());

        return position;
    }

    private List<Position> startDecoding(Channel channel, SocketAddress remoteAddress, String sentence) throws Exception  {
        List<Position> positions = new LinkedList<>();

        String[] fragments = sentence.split("#|,\\*");
        for (int index = 0; index < fragments.length; index++) {
            Pattern pattern = PATTERN_EVENT;
            Parser parser = new Parser(pattern, fragments[index]);
            Position position = new Position(getProtocolName());
            if (!parser.matches()) {
                decodeYear(position, channel, remoteAddress, fragments[index]);
            } else {
                decodeEvent(parser, position, channel, remoteAddress);
            }
            if (position.getLatitude() != 0 && position.getLongitude() != 0) {
                positions.add(position);
                sendResponse(channel, remoteAddress, "Ok");
            } else {
                if (!positions.isEmpty()) {
                    position = positions.get(positions.size() - 1);
                } else {
                    DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
                    if (deviceSession == null) {
                        continue;
                    }
                }
                getLastLocation(position, null);
                positions.add(position);
                sendResponse(channel, remoteAddress, String.format("%s", fragments[index]));

            }
        }
        if (channel != null && positions.isEmpty()) {
            LOGGER.warn("NOT DECODED:" + sentence);
        }

        return positions;
    }

    private  void sendResponse(Channel channel, SocketAddress remoteAddress, String response) {
        if (channel != null) {
            channel.writeAndFlush(new NetworkMessage(
                    Unpooled.copiedBuffer(response, StandardCharsets.US_ASCII), remoteAddress));
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        String sentence = ((ByteBuf) msg).toString(StandardCharsets.US_ASCII);

        return startDecoding(channel, remoteAddress, sentence);
    }
}

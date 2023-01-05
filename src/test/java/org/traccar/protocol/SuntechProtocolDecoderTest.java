package org.traccar.protocol;

import org.junit.Ignore;
import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Position;

public class SuntechProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecodeTemperature() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        decoder.setHbm(true);
        decoder.setIncludeAdc(true);

        verifyAttribute(decoder, buffer(
                "ST600STT;008594432;20;492;20200212;18:58:30;060bb0e1;334;20;36bb;45;+19.337897;-099.064489;000.398;000.00;12;1;5049883;13.61;100100;2;1198;013762;4.2;1;4.68"),
                Position.PREFIX_ADC + 1, 4.68);

        decoder.setIncludeTemp(true);

        verifyAttribute(decoder, buffer(
                "ST600STT;008350848;35;523;20191102;13:49:46;0bf14fdb;334;20;2f19;57;+20.466737;-100.825455;000.006;000.00;11;1;10274175;11.36;00000000;1;0300;018353;4.2;1;0.00;;;;00000000000000;0;28EE56B911160234:+13.7;:;:"),
                Position.PREFIX_TEMP + 2, 13.7);

        verifyPosition(decoder, buffer(
                "ST300STT;205173382;07;564;20160322;23:23:18;232e19;+19.288278;-099.128750;000.122;000.00;9;1;478391;11.53;000100;1;9498;079324;4.3;1;0.00;0.00;0.00;00000000000000;0;2898E16006000058:-20.8;2861626006000039:+2.5;:"));

        verifyPosition(decoder, buffer(
                "ST300EVT;205173382;07;564;20160322;23:23:18;232e19;+19.288278;-099.128750;000.122;000.00;9;1;478391;11.53;000100;2;1;9498;079324;4.3;1;0.00;0.00;0.00;00000000000000;0;2898E16006000058:-20.8;2861626006000039:+2.5;:"));

        verifyPosition(decoder, buffer(
                "ST600STT;008349958;35;523;20181112;00:49:30;0bf10d4e;334;20;2f19;22;+20.552718;-100.824478;050.021;095.41;12;1;1303603;14.30;10000000;4;8911;001987;4.1;0;0.00;;;;00000000000000;0;2826C8A70800000C:+26.6;:;:"));

    }

    @Test
    public void testDecodeRpm() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        decoder.setHbm(true);
        decoder.setIncludeRpm(true);

        verifyAttribute(decoder, buffer(
                "ST300STT;907131077;04;706;20190227;23:59:34;cc719;-12.963490;-038.499587;000.067;000.00;7;1;57095;12.50;000000;1;0337;000207;0.0;1;0;012E717F010000;1"),
                Position.KEY_RPM, 0);

    }

    @Test
    public void testDecodeHours() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        decoder.setHbm(true);

        verifyAttribute(decoder, buffer(
                "ST300ALT;007239104;40;313;20190112;01:07:16;c99139;+04.703287;-074.148897;000.000;189.72;21;1;425512;12.61;100000;33;003188;4.1;1"),
                Position.KEY_HOURS, 3188 * 60000L);

    }

    @Test
    public void testDecodeDriver() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        verifyAttribute(decoder, buffer(
                "ST300HTE;511050566;45;308;20200909;13:38:38;0;12.50;001354;0.0;1;0;1;1;0;-27.636632;-052.277933;-27.636675;-052.277947;000.000;002.296;0;00000000000000"),
                Position.KEY_DRIVER_UNIQUE_ID, "00000000000000");

        verifyAttribute(decoder, buffer(
                "ST300HTE;100850001;04;248;20110101;00:13:52;167559;12.28;004005;0.0;1;0;3;3;0;-22.881018;-047.070831;-22.881018;-047.070831;000.000;000.000;0;0;3;0;0;0;01E04D44160000"),
                Position.KEY_DRIVER_UNIQUE_ID, "01E04D44160000");

    }

    @Test
    public void testDecode() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        verifyAttribute(decoder, buffer(
                "ALT;0950030205;3FFFFF;95;1.0.11;0;20221017;21:41:30;02F2F402;334;20;0F1D;45;+25.791061;-100.170745;0.00;0.00;18;1;00000101;00000000;42;2;"),
                Position.KEY_ALARM, Position.ALARM_SOS);

        verifyAttribute(decoder, buffer(
                "RES;4309999001;04;02;TEST"),
                Position.KEY_RESULT, "04;02;TEST");

        verifyPosition(decoder, buffer(
                "BLE;1140000053;114;1.0.1;20211001;17:27:09;+28.433465;-82.565891;1;-43;-46;-41;ACB89523EF68;247;0;0"));

        verifyPosition(decoder, buffer(
                "BLE;0820012345;82;1.0.0;20191203;17:00:51;+32.691615;-117.297160;2;-32;-100;33;AABBCCDDEEFF;12;18;52;1;-44;44;112233445566;32;69;101"));

        verifyNull(decoder, buffer(
                "BSA;0820012345;001FFF;82;1.0.0;1;20191203;17:00:51;+32.691615;-117.297160;1;-55;68:11:6A:FD:1A:A7;6AA5;1DE8"));

        verifyAttributes(decoder, binary(
                "53543330305545583b3531313639393339383b34353b3331353b32303232303632333b31383a32343a35383b3661313332393b2d32392e3735343934373b2d3035372e3038353838353b3030302e3030303b3030302e30303b31303b313b37323b302e30303b3030303030303b31323b0201100110011090011001100110011001100110fe3b39303b3030303030303b332e393b313b30303030303030303030303030303b30"));

        verifyAttribute(decoder, buffer(
                "ST300UEX;511248287;45;311;20220701;18:42:08;14c943;-22.975257;-043.373065;000.000;000.00;0;0;0;12.14;100010;19;RFID:008FB2BEBA39\r\n;3E;000135;4.1;1;00000000000000;0"),
                "serial", "RFID:008FB2BEBA39");

        verifyAttribute(decoder, buffer(
                "ST300UEX;100850000;01;010;20081017;07:41:56;2F100;+37.478519;+126.886819;000.012;000.00;9;1;0;15.30;001100;25;Welcome to Suntech World!;12;0;4.5;1"),
                "serial", "Welcome to Suntech World!");

        verifyAttribute(decoder, buffer(
                "ST300UEX;511331307;45;311;20210420;12:41:01;12361;-01.280825;-047.931773;000.000;000.00;16;1;0;12.54;000000;23;GTSL|6|1|0|9255143|2|;6F;000276;0.0;1;00000000000000;0"),
                Position.KEY_DRIVER_UNIQUE_ID, "9255143");

        verifyAttribute(decoder, buffer(
                "STT;0560001616;BFFFFF;56;1.0.15;1;20200219;20:52:25;00008D6C;334;20;0925;24;+20.741764;-103.430364;0.00;0.00;19;1;00000001;00000000;2;1;1765;00008003;0.0;12.14;136598"),
                Position.KEY_INDEX, 1765);

        verifyAttribute(decoder, buffer(
                "STT;0560001616;BFFFFF;56;1.0.15;1;20200219;20:52:25;00008D6C;334;20;0925;24;+20.741764;-103.430364;0.00;0.00;19;1;00000001;00000000;2;1;1765;00008003;0.0;12.14;136598"),
                Position.KEY_INPUT, 1);

        verifyAttribute(decoder, binary(
                "82004d05800000553fffff360100100114020410293902ccccf102dc007b00053c00476fa18469e87f000000000b0100003b00081d00000113f3f8010000049e00000000000000001d00000113f3f801"),
                Position.KEY_DRIVER_UNIQUE_ID, "1d00000113f3f801");

        verifyPosition(decoder, buffer(
                "ST410STT;007638094;426;01;24153;724;4;-65;365;0;24161;724;4;365;0;0;24162;724;4;365;0;0;24363;724;4;365;0;0;24151;724;4;365;0;0;24991;724;4;365;0;0;24373;724;4;365;0;0;3.98;1;0176;2;016;20200106;19:18:04;-15.571860;-056.062637;000.852;238.28;6;1;201"));

        verifyPosition(decoder, buffer(
                "ST390STT;007579860;18;302;20191101;11:28:51;145b49;-23.267030;-047.298142;000.000;000.00;9;1;5;11.93;000000;2;0003;000002;4.03;0;20010000;22470;724;05;-58;5211;1"));

        verifyAttribute(decoder, binary(
                "81004e05200013383fffff3401000300130b020b2a0500000000000000000000000047f9ec846a06500000000012010200000123a1002904ba00010fb40000000000000000000000000000000000005989"),
                Position.KEY_IGNITION, false);

        verifyPosition(decoder, binary(
                "81004e05200013383fffff3401000301130a0512080400000000000000000000000047f9d5846a06810072225214010100020300a8002604c1000004b000000470000025a100000000000025c4000000a6"));

        verifyPosition(decoder, buffer(
                "ALT;0520000295;3FFFFF;52;1.0.2;0;20190703;01:03:24;00004697;732;101;0002;59;+4.682583;-74.128142;0.00;0.00;6;1;00000000;00000000;9;1;;4.1;12.92;103188"));

        verifyAttribute(decoder, buffer(
                "ST600UEX;008728327;20;568;20200602;17:10:03;0bf1a893;334;20;2f19;23;+20.514492;-100.743221;000.033;000.00;12;1;41564973;13.17;000000;44;t_0=1C;N_0=0419.0;t_1=22;N_1=0001.0;Q_D=09\n\r;76;113771;4.1;1"),
                Position.KEY_FUEL_LEVEL, 1050.0);

        verifyAttribute(decoder, buffer(
                "ST300UEX;109003241;08;1026;20190425;17:36:04;04402;+04.722553;-074.052583;000.020;000.00;10;1;0;12.04;010000;51;CabAVL\"CabMensaje,0,58.5,-1.0,,,FinMensaje\"FinAVL\r\n;B1;0000000000;4.1;1"),
                "fuel1", 58.5);

        verifyPosition(decoder, buffer(
                "ST600UEX;008728327;20;520;20190218;10:56:51;0bf1a893;334;20;2f19;18;+20.514195;-100.743597;000.015;000.00;9;1;3720808;12.89;000000;44;t_0=0D;N_0=0551.0;t_1=14;N_1=039F.0;Q_D=0B\r\n;9E;010440;4.1;1"));

        verifyPosition(decoder, buffer(
                "ST600UEX;100850000;01;010;20081017;07:41:56;0000004f;450;20;0023;24;+37.478519;+126.886819;000.012;000.00;9;1;0;15.30;001100;25;Welcome to Suntech World!;12;0;4.5;1"));

        verifyPosition(decoder, buffer(
                "ST300STT;007238270;40;313;20190220;12:05:04;c99e48;+04.644623;-074.076922;010.390;202.77;20;1;997100;14.10;100000;2;8384;003634;4.1;1"));

        verifyPosition(decoder, buffer(
                "ST300STT;109002029;08;1080;20190220;13:00:55;85405;+04.645710;-074.078525;007.760;005.19;10;1;6520802;13.86;100100;4;1716;0000039863;4.1;1;0.00;0000;0000;0;0"));

        verifyPosition(decoder, buffer(
                "SA200STT;608945;129;20190215;15:04:53;3dce071558;+22.006721;-098.771016;001.198;000.00;11;1;2632589;12.21;010000;1;3211"));

        verifyPosition(decoder, buffer(
                "ST410STT;007272376;408;01;10217;732;103;-87;51511;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;3.8;1;2503;6;20181031;20:12:58;+04.741277;-074.048238;052.375;189.87;20;1"));

        verifyPosition(decoder, buffer(
                "ST410STT;007272376;408;01;21651;732;123;-65;1824;1;21654;732;123;1824;0;0;22542;732;123;1824;0;0;21656;732;123;1824;0;0;21655;732;123;1824;0;0;22541;732;123;1824;0;0;0;0;0;0;0;0;3.7;1;0156;1;20180816;05:18:52;+04.722322;-074.052776;000.074;000.00;10;1"));

        verifyPosition(decoder, buffer(
                "ST600STT;008084783;20;419;20180308;18:00:36;0032cc3e;736;3;445c;41;-16.530023;-068.084267;018.640;267.99;10;1;11655;13.33;100000;2;0336;000061;4.5;0;0.00"));

        verifyPosition(decoder, buffer(
                "ST600STT;107850496;20;419;20180227;14:30:45;00462b08;736;3;4524;50;-16.479091;-068.119119;000.346;000.00;4;1;0;13.89;000000;1;0223;000003;0.0;0;0.00"));

        verifyPosition(decoder, buffer(
                "ST600STT;100850000;01;010;20081017;07:41:56;0000004f;450;20;0023;24;+37.478519;+126.886819;000.012;000.00;9;1;0;15.30;00110000;1;0072;0;4.5;1;12.35"));

        verifyPosition(decoder, buffer(
                "STT;100850000;3FFFFF;26;010;1;20161117;08:37:39;0000004F;450;0;0014;20;+37.479323;+126.887827;62.03;65.43;10;1;00000101;00001000;1;2;0492"));

        verifyPosition(decoder, buffer(
                "STT;6009999006;3FFFFF;26;398;0;20170827;20:04:37;087d4760;310;410;0ba0;23;+40.123420;-074.995971;000.031;000.00;8;1;00000001;00000000;1;1;0006"));

        verifyPosition(decoder, buffer(
                "ST500STT;205450135;07;843;20170816;23:24:45;+19.338432;-099.179817;000.283;000.00;6;1;141121;12.89;0;0;1;4659;002.795;0;001.891;611;4.0"));

        verifyPosition(decoder, buffer(
                "ST300STT;205170303;12;561;20170816;09:10:34;173f53;+19.082370;-098.214287;006.776;000.00;0;0;52982186;12.75;100000;2;6328;155747;4.2;1;0.00;0;0.00;0.00;00000000000000;0;28F2B7600600005D:+5.2;:;:"));

        verifyPosition(decoder, buffer(
                "ST910;Location;205576803;500;20170319;12:18:17;-22.846014;-046.322176;000.000;000.00;0;3.8;0;1;9159"));

        verifyPosition(decoder, buffer(
                "ST910;Emergency;205576803;500;20170319;12:15:22;-22.846014;-046.322176;000.000;000.00;0;2"));

        verifyPosition(decoder, buffer(
                "ST910;Location;205576803;500;20170312;12:56:52;-22.846014;-046.322176;000.000;000.00;0;3.8;0;0;0019"));

        verifyPosition(decoder, buffer(
                "ST300STT;100850000;01;010;20081017;07:41:56;00100;+37.478519;+126.886819;000.012;000.00;9;1;0;15.30;001100;1;0072;0;4.5;1;1750;012497F1160000;1;004f001454;450;00;-320;20;255;1"));

        verifyPosition(decoder, buffer(
                "ST300STT;205589913;05;527;20170304;02:21:33;be139;-25.398868;-049.325636;000.476;000.00;6;1;427;12.57;100000010;1;0172;017.159;0;002.327;12;4.0"));

        verifyPosition(decoder, buffer(
                "SA200STT;638947;803;20170117;07:40:44;5d309;-01.287213;-047.917462;000.035;000.00;10;1;2036194;12.57;000000;1;0376;010360;4.2;1"));

        verifyPosition(decoder, buffer(
                "ST300ALT;205174410;14;712;20110101;00:00:07;00000;+20.593923;-100.336716;000.000;000.00;0;0;0;16.57;000000;81;000000;4.0;0;0.00;0000;0000;0;0"));

        verifyNull(decoder, buffer(
                "SA200ALV;317652"));

        verifyPosition(decoder, buffer(
                "ST910;Alert;123456;410;20141018;18:30:12;+37.478774;+126.889690;000.000;000.00;0;4.0;1;6002"),
                position("2014-10-18 18:30:12.000", false, 37.47877, 126.88969));

        verifyPosition(decoder, buffer(
                "ST910;Alert;123456;410;20141018;18:30:12;+37.478774;+126.889690;000.000;000.00;0;4.0;1;6002;02;0;0310000100;450;01;-282;70;255;3;0"));

        verifyPosition(decoder, buffer(
                "SA200STT;317652;042;20120718;15:37:12;16d41;-15.618755;-056.083241;000.024;000.00;8;1;41548;12.17;100000;2;1979"),
                position("2012-07-18 15:37:12.000", true, -15.61876, -56.08324));

        verifyPosition(decoder, buffer(
                "SA200STT;317652;042;20120721;19:04:30;16d41;-15.618743;-056.083221;000.001;000.00;12;1;41557;12.21;000000;1;3125"));

        verifyPosition(decoder, buffer(
                "SA200STT;317652;042;20120722;00:24:23;4f310;-15.618767;-056.083214;000.011;000.00;11;1;41557;12.21;000000;1;3205"));

        verifyPosition(decoder, buffer(
                "SA200STT;315198;042;20120808;20:37:34;3fac25;-15.618731;-056.083216;000.007;000.00;12;1;48;0.00;000000;1;0127"));

        verifyPosition(decoder, buffer(
                "SA200STT;315198;042;20120809;13:43:34;4f310;-15.618709;-056.083223;000.025;000.00;8;1;49;12.10;100000;2;0231"));

        verifyPosition(decoder, buffer(
                "SA200EMG;317652;042;20120718;15:35:41;16d41;-15.618740;-056.083252;000.034;000.00;8;1;41548;12.17;110000;1"));

        verifyPosition(decoder, buffer(
                "SA200ALT;317652;042;20120829;14:25:58;16d41;-15.618770;-056.083242;000.029;000.00;0;0;2404240;0.00;000000;10"));

        verifyPosition(decoder, buffer(
                "SA200STT;430070;133;20130615;22:22:32;151347;+02.860514;-060.653351;000.003;000.00;12;1;0;12.39;000000;1;0208"));

        verifyPosition(decoder, buffer(
                "ST910;Location;344506;017;20130727;14:10:00;-25.398714;-049.296818;000.187;000.00;1;4.32;1;1;0001"));

        verifyPosition(decoder, buffer(
                "ST300STT;205027329;03;374;20150108;17:54:42;177b38;-23.566052;-046.477588;000.000;000.00;0;0;0;12.11;000000;1;0312"));
        
        verifyPosition(decoder, buffer(
                "ST910;Emergency;205283272;500;20150716;19:12:01;-23.659019;-046.695403;000.602;000.00;0;4.2;1;1;02;10820;2fdb090736;724;05;0;2311;255;0;100"));

        decoder.setProtocolType(1);

        verifyPosition(decoder, buffer(
                "ST910;Location;907510186;552;20180504;23:15:45;3af54e5331;+19.301833;-099.190657;000.246;000.00;1;28462;80;1;0;0423;02;334;05;-215;20051;1;4;100"));

        verifyPosition(decoder, buffer(
                "ST910;Alert;485195;20170409;22:37:41;3be0133057;+24.882410;-107.509152;000.070;000.00;1;286734;72;02;295;05;-415;4912;255;10;10"));

        verifyPosition(decoder, buffer(
                "ST910;Location;485195;528;20170410;01:18:57;f1dd134840;+24.787139;-107.434679;000.020;000.00;1;286734;100;1;0;0188;02;295;05;-339;4936;255;4;74"));

        verifyPosition(decoder, buffer(
                "ST910;Location;560266;500;20161207;21:33:11;af910be101;-25.504234;-049.278003;000.080;000.00;1;10054889;70;1;1;1311;02;724;06;-317;3041;2;10;92"));

        verifyPosition(decoder, buffer(
                "ST910;Emergency;238569;528;20170403;00:02:09;7574160020;+19.661292;-099.144473;000.176;000.00;1;228638;1"));

    }

    @Ignore
    @Test
    public void testDecodeCrash() throws Exception {

        var decoder = inject(new SuntechProtocolDecoder(null));

        verifyAttribute(decoder, binary(
                "4352523b303931303030303036333b313b313b303135303b16011c150f0ad82f6c0000000000ae037085fbff7700fd00faff6300f30000006800fb000d007100fa00f32f6c00000000005e044a80fcff6f000301e1ff5d00e900e1ff6400e600f4ff5b00ec000a306c00000000002104248306006c000501fcff5b00e00001006e000101eeff4e00e10022306c00000000005c041a7e00006a00010100005d00f800b5ff7cffdf0050009300fc003b44350d"),
                Position.KEY_G_SENSOR, "");

    }

}
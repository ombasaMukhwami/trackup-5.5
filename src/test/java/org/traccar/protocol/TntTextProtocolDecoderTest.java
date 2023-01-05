package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Position;


public class TntTextProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        var decoder = inject(new TntTextProtocolDecoder(null));


        verifyPositions(decoder, buffer(
                "01/24/2020,07:56:30,863753042146951,TMC,KCP981M,0,-1.0428,S,37.091016,E,1,1,1#"));

        verifyPositions(decoder, buffer(
                "01/24/2020,08:29:30,863753042146951,TMC,KCP981M,0,-1.042783,S,37.09105,E,1,1,0#"));

        verifyPositions(decoder, buffer(
                "2019-07-31,09:47:10,860675049563586,A3E-9BHGA3,KCA379D,0,134,2,0,36.830787,E(+),1.301381,S(-),0,0,0 2019-07-31,09:47:40,860675049563586,A3E-9BHGA3,KCA379D,0,134,2,0,36.830787,E(+),1.301381,S(-),0,0,0\n"));

        verifyAttribute(decoder, buffer(
                        "2019-07-30,12:28:46,860675049563586,A3E-9BHGA3,KCA379D,0,122,1,16,36.820249,E(+),1.267266,S(-),0,0,0,A11\n"),
                Position.KEY_ALARM, Position.ALARM_GPS_ANTENNA_CUT);

        verifyAttribute(decoder, buffer(
                        "2020-07-28,06:43:47,862237047471037,A3E-9BHGA3,KAL018K,27,1578,1,20,36.859861,E(+),1.264481,S(-),0,0,0#"),
                Position.KEY_IGNITION, true);

        verifyAttribute(decoder, buffer(
                        "2019-07-30,12:28:47,860675049563586,A3E-9BHGA3,KCA379D,0,122,1,16,36.820249,E(+),1.267266,S(-),0,0,0,A12\n"),
                Position.KEY_ALARM, Position.ALARM_GPS_RECONNECTED);

        verifyAttribute(decoder, buffer(
                        "2019-07-31,08:31:20,860675049563586,A3E-9BHGA3,KCA379D,0,125,1,12,36.820312,E(+),1.267489,S(-),0,0,1,A02\n"),
                Position.KEY_ALARM, Position.ALARM_POWER_OFF);

        verifyPositions(decoder, buffer(
                "2019-07-30,10:18:51,860675049563586,A3E-9BHGA3,KCA379D,0,122,1,13,36.820249,E(+),1.267266,S(-),0,0,0"));
        verifyPositions(decoder, buffer(
                "2019-10-15,08:44:53,860675048566622,A3E-9BHGA3,,0,0,2,0,36.818204,E(+),1.267439,S(-),1,0,1,A05#"));

        verifyPositions(decoder, buffer(
                "*20/08/2019,12:58:50,990000862471845,GV3,R23828,325.000,0121.82849,S,03657.53074,E,0,0,0,0,0,27.5,1,80,80,0,*"));

        verifyPositions(decoder, buffer(
                "08/27/19,02:15:00,60298040118944,GV4,KCJ 948U,0,1.217283,S,36.883700,E,0,1,0,0,0"));
        verifyPositions(decoder, buffer(
                "10/04/19,09:24:32,862868045768089,GV4,KBK 791V,45,0.43433,N,37.655083,E,0,0,0,1,0#"));
        verifyPositions(decoder, buffer(
                "2022-12-23,18:50:15,868053051463859,HB-A3,KBQ996W,0,14589,1,21,36.874844,E(+),1.335924,S(-),0,0,0#"));

        verifyPositions(decoder, buffer(
                "08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0\r08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0\r08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0#"));


        verifyPositions(decoder, buffer(
                "08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0#08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0#08/13/19,08:57:48,860298040119173,GV4,KBK 791V,37,1.318650,S,36.770783,E,0,0,229,1,0#"));
    }

}

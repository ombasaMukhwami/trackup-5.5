package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class HuabaoFrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        var decoder = inject(new HuabaoFrameDecoder());

        verifyFrame(
                binary("283734303139303331313138352c312c3030312c454c4f434b2c332c35323934333929"),
                decoder.decode(null, null, binary("283734303139303331313138352c312c3030312c454c4f434b2c332c35323934333929")));

        verifyFrame(
                binary("7e307e087d557e"),
                decoder.decode(null, null, binary("7e307d02087d01557e")));

        verifyFrame(
                binary("323032322d31322d32342c31373a35363a33322c3836383035333035313436333835392c48422d41332c4b4251393936572c302c31343631382c312c32302c33362e3838303132342c45282b292c312e3334343330382c53282d292c302c302c3023"),
                decoder.decode(null, null, binary("323032322d31322d32342c31373a35363a33322c3836383035333035313436333835392c48422d41332c4b4251393936572c302c31343631382c312c32302c33362e3838303132342c45282b292c312e3334343330382c53282d292c302c302c3023")));


    }

}

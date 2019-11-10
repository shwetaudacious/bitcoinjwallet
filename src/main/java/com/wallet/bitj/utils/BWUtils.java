package com.wallet.bitj.utils;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

public class BWUtils {

	public NetworkParameters getNetworkParams(String net) {
		NetworkParameters netparam= (StringUtils.equalsIgnoreCase(net, "prod")) ?  MainNetParams.get() : TestNet3Params.get();
		return netparam;
	}
}

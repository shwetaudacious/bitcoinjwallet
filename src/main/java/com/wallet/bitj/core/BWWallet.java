package com.wallet.bitj.core;
import java.io.File;
import java.io.IOException;

/**
 * 
 * @author Shweta
 *
 */
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import com.wallet.bitj.utils.BWUtils;

public class BWWallet {
	private String net ="test";
	private File walletFile;
	
	@Autowired
	BWUtils utils;
	
	public File getWalletFile() {
		return walletFile;
	}

	public void setWalletFile(File walletFile) {
		this.walletFile = walletFile;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}
	
	/**
	 * generate BTC address
	 * @param arg
	 * @return
	 */
	private Address getBTCAddress() {
		System.out.println("using "+ getNet());
		ECKey key = new ECKey(); //generate elliptical key
		NetworkParameters netparam = utils.getNetworkParams(net); 
		return key.toAddress(netparam); // generates address based on test or prod network
	}
	
	private Wallet createWalllet() {
		NetworkParameters netparam = utils.getNetworkParams(net); // identity the network
		Wallet wallet = null;
		try {
			wallet = new Wallet(netparam); // generate wallet based on the network
			for (int i = 0; i < 5; i++) {
				wallet.importKey(new ECKey());
			}
			wallet.saveToFile(walletFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("wallet"+ wallet);
		return wallet;
	}
	
}

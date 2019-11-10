package com.wallet.bitj.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.springframework.beans.factory.annotation.Autowired;

import com.wallet.bitj.utils.BWUtils;

public class BWBlock {
	private String net ="test";
	@Autowired
	BWUtils utils;
	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}
	private Block fetchBlock(String hash) {
	NetworkParameters netparam = utils.getNetworkParams(net); // identity the network
	BlockStore blockStore = new MemoryBlockStore(netparam);
	BlockChain chain;
	try {
		chain = new BlockChain(netparam, blockStore);
		final Peer peer = new Peer(netparam, new VersionMessage(netparam, 0), new PeerAddress(InetAddress.getLocalHost()), chain);
		peer.connectionOpened();
		Sha256Hash blockhash = new Sha256Hash( hash);
		Future<Block> future = peer.getBlock(blockhash);
		Block block = future.get();
		peer.close();
		return block;
	} catch (BlockStoreException e) {
		e.printStackTrace();
	} catch (InterruptedException e) {
		e.printStackTrace();
	} catch (ExecutionException e) {
		e.printStackTrace();
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	return null;
		
	}
}

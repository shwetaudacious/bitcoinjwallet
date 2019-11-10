package com.wallet.bitj.core;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VersionMessage;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.util.concurrent.ListenableFuture;
import com.wallet.bitj.utils.BWUtils;

/**
 * 
 * @author Shweta
 *
 */
public class BWCoins {
	private String net ="test";
	File walletFile;
	String amt;
	String recipient;
	
	public File getWalletFile() {
		return walletFile;
	}

	public void setWalletFile(File walletFile) {
		this.walletFile = walletFile;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Autowired
	BWUtils utils;
	
	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}
	
	private boolean sendCoins() {
		NetworkParameters netparam = utils.getNetworkParams(net); // identity the network
		BlockStore blockStore = new MemoryBlockStore(netparam); //storage
		Wallet wallet;
		BlockChain chain;
		SendRequest request =SendRequest.to(Address.fromBase58(netparam, recipient), Coin.parseCoin(amt));
		try {
			wallet = Wallet.loadFromFile(walletFile);
			chain = new BlockChain(netparam, wallet, blockStore);
			final Peer peer = new Peer(netparam, new VersionMessage(netparam, 0), new PeerAddress(InetAddress.getLocalHost()), chain);
			peer.connectionOpened();
			Address.fromBase58(netparam, recipient);
			Transaction txn = wallet.sendCoins(peer, request);
			wallet.saveToFile(walletFile);
			peer.connectionClosed();
		} catch (UnreadableWalletException e) {
			e.printStackTrace();
		} catch (BlockStoreException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InsufficientMoneyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
		
	}
	
	private List<Transaction> fetchTransactions(String hexString) {
		NetworkParameters netparam = utils.getNetworkParams(net); // identity the network
		BlockStore blockStore = new MemoryBlockStore(netparam);
		try {
			BlockChain chain = new BlockChain(netparam, blockStore);
			final Peer peer = new Peer(netparam, new VersionMessage(netparam, 0),
					new PeerAddress(InetAddress.getLocalHost()), chain);
			peer.connectionOpened();
			Sha256Hash txHash = Sha256Hash.wrap(hexString);
			ListenableFuture<Transaction> future = peer.getPeerMempoolTransaction(txHash);
			Transaction tx = future.get();
			List<Transaction> deps = peer.downloadDependencies(tx).get();
			for (Transaction dep : deps) {
				System.out.println("Got dependency " + dep.getHashAsString());
			}
			peer.connectionClosed();
			return deps;
		} catch (BlockStoreException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

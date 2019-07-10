package nl.popkoortheinspiration.programmamaker.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import nl.popkoortheinspiration.programmamaker.shared.Song;

@RemoteServiceRelativePath("repertoire")
public interface IRepertoireDB extends RemoteService {
	Song[] getSongs();
	
}

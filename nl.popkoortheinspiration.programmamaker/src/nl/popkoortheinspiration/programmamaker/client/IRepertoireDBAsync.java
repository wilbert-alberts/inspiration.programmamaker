package nl.popkoortheinspiration.programmamaker.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import nl.popkoortheinspiration.programmamaker.shared.Song;

public interface IRepertoireDBAsync {

	void getSongs(AsyncCallback<Song[]> callback);

}

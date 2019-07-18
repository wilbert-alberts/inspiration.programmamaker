package nl.popkoortheinspiration.programmamaker.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import nl.popkoortheinspiration.programmamaker.shared.Song;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nl_popkoortheinspiration_programmamaker implements EntryPoint {
	Logger logger = Logger.getLogger("Com_inspiration.class");

	private RepertoireList repertoireList = new RepertoireList();
	private Setlist setlist = new Setlist();
	private IRepertoireDBAsync stockPriceSvc = GWT.create(IRepertoireDB.class);

	private CellList<String> clRepertoire;

	public void onModuleLoad() {
		Button toSetlist = new Button("toevoegen");
		Button fromSetlist = new Button("verwijderen");
		Button addBreak = new Button("pauze");
		Button upButton = new Button("eerder");
		Button downButton = new Button("later");
		Button sendButton = new Button("Versturen");
		
		fillRepertoire();

		toSetlist.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.log(Level.FINEST, "toSetlist::onClick");
				Song song = repertoireList.getSelectedSong();
				repertoireList.addSongPerformance(song);
				setlist.addSong(song);
				updateTotalLength();
			}
		});

		fromSetlist.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Song item = setlist.removeSelectedItem();
				if (item != null) {
					repertoireList.removeSongPerformance(item);
				}
				updateTotalLength();
			}
		});

		upButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setlist.moveUp();
			}
		});

		downButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setlist.moveDown();
			}
		});

		addBreak.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				logger.log(Level.FINEST, "addBreak::onClick");
				setlist.addBreak();
			}
		});

		sendButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				sendMail();
			}
		});

//		lbSetList.addDoubleClickHandler(new DoubleClickHandler() {
//
//			@Override
//			public void onDoubleClick(DoubleClickEvent event) {
//				setlist.removeSelectedItem();
//				updateTotalLength();
//			}
//		});

/*
		lbRepertoire.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				int idx = lbRepertoire.getSelectedIndex();
				setlist.addSong(repertoire.get(idx));
				updateTotalLength();
			}
		});
*/
		VerticalPanel contentPanel = new VerticalPanel();
		VerticalPanel orderPanel = new VerticalPanel();

		contentPanel.add(toSetlist);
		contentPanel.add(fromSetlist);
		contentPanel.add(addBreak);
		orderPanel.add(upButton);
		orderPanel.add(downButton);

		RootPanel.get("replist").add(repertoireList.getWidget());
		RootPanel.get("contentbuttons").add(contentPanel);
		RootPanel.get("setlist").add(setlist.getWidget());
		RootPanel.get("orderbuttons").add(orderPanel);
		RootPanel.get("sendbutton").add(sendButton);

	}

	private void fillRepertoire() {
		if (stockPriceSvc == null) {
			stockPriceSvc = GWT.create(IRepertoireDB.class);
		}

		AsyncCallback<Song[]> callback = new AsyncCallback<Song[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Song[] result) {
				Comparator<Song> dateTitleComp = new Comparator<Song>() {

					@Override
					public int compare(Song o1, Song o2) {
						int r = o1.getInRepertoireSince().compareTo(o2.getInRepertoireSince());
						if (r == 0) {
							r = o1.getTitle().compareTo(o2.getTitle());
						}
						return r;
					}
				};
				TreeSet<Song> regularSongs = new TreeSet<Song>(dateTitleComp);
				TreeSet<Song> christmasSongs = new TreeSet<Song>(dateTitleComp);

				for (Song song : result) {
					if (song.isChristmasSong()) {
						christmasSongs.add(song);
					} else {
						regularSongs.add(song);
					}
				}

				List<Song> repList = new ArrayList<Song>();
				repList.addAll(regularSongs);
				repList.addAll(christmasSongs);
				repertoireList.initialize(repList);
			}
		};

		stockPriceSvc.getSongs(callback);
	}

	private void updateTotalLength() {
		RootPanel.get("totallength").getElement().setInnerText(setlist.getDurationAsString());

	}

	private void sendMail() {
	}
}

package nl.popkoortheinspiration.programmamaker.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import nl.popkoortheinspiration.programmamaker.shared.Setlist;
import nl.popkoortheinspiration.programmamaker.shared.Song;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nl_popkoortheinspiration_programmamaker implements EntryPoint {
	Logger logger = Logger.getLogger("Com_inspiration.class");

	private ListBox lbRepertoire = new ListBox();
	private ListBox lbSetList = new ListBox();
	private Setlist setlist = new Setlist(lbSetList);
	private List<Song> repertoire = new ArrayList<Song>();
	private IRepertoireDBAsync stockPriceSvc = GWT.create(IRepertoireDB.class);

	public void onModuleLoad() {
		Button toSetlist = new Button("toevoegen");
		Button fromSetlist = new Button("verwijderen");
		Button addBreak = new Button("pauze");
		Button upButton = new Button("eerder");
		Button downButton = new Button("later");
		Button sendButton = new Button("Versturen");
		lbRepertoire.setVisibleItemCount(30);
		lbSetList.setVisibleItemCount(30);
		fillRepertoire();

		toSetlist.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.log(Level.FINEST, "toSetlist::onClick");
				int idx = lbRepertoire.getSelectedIndex();
				setlist.addSong(repertoire.get(idx));
				updateTotalLength();
			}
		});

		fromSetlist.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = lbSetList.getSelectedIndex();
				setlist.remove(idx);
				updateTotalLength();
			}
		});

		upButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = lbSetList.getSelectedIndex();
				setlist.moveUp(idx);
			}
		});

		downButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int idx = lbSetList.getSelectedIndex();
				setlist.moveDown(idx);
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
		
		lbSetList.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				int idx = lbSetList.getSelectedIndex();
				setlist.remove(idx);
				updateTotalLength();
			}
		});
		
		lbRepertoire.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				int idx = lbRepertoire.getSelectedIndex();
				setlist.addSong(repertoire.get(idx));
				updateTotalLength();
			}
		});
		
		VerticalPanel contentPanel = new VerticalPanel();
		VerticalPanel orderPanel = new VerticalPanel();

		contentPanel.add(toSetlist);
		contentPanel.add(fromSetlist);
		contentPanel.add(addBreak);
		orderPanel.add(upButton);
		orderPanel.add(downButton);

		RootPanel.get("replist").add(lbRepertoire);
		RootPanel.get("contentbuttons").add(contentPanel);
		RootPanel.get("setlist").add(lbSetList);
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

				for (Song song : regularSongs) {
					repertoire.add(song);
					lbRepertoire.addItem(song.getTitleAndDuration());
				}
				for (Song song : christmasSongs) {
					repertoire.add(song);
					lbRepertoire.addItem(song.getTitleAndDuration());
				}
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

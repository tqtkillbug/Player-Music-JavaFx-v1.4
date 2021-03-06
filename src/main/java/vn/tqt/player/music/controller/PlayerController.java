package vn.tqt.player.music.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.json.simple.parser.ParseException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import vn.tqt.player.music.model.Playlist;
import vn.tqt.player.music.model.Song;
import vn.tqt.player.music.initWindow.InitAlertWindow;
import vn.tqt.player.music.repository.IPlaylistRepository;
import vn.tqt.player.music.repository.PlaylistRepository;
import vn.tqt.player.music.services.GetTitleSong;
import vn.tqt.player.music.services.IPlaylistService;
import vn.tqt.player.music.services.PlaylistService;

public class PlayerController implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TextField namePlaylist;
    @FXML
    private ImageView logoSong;
    @FXML
    private Label singerName;
    @FXML
    private Label songName;
    @FXML
    private Label songTime;
    @FXML
    private Button playButton, pauseButton, nextButton, perviousButton, loopButton, randomButton, createPlaylist, newPlaylistbtn;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private ComboBox<String> playlistBox;
    @FXML
    private Slider volumeBar;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TableView<Song> table;
    @FXML
    private TableColumn<Song, Integer> idColumn;
    @FXML
    private TableColumn<Song, String> nameColumn;
    private ObservableList<Song> songList;
    private ObservableList<Playlist> playlistList;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private File musicDirectory;
    @FXML
    private File allMusicDirectory;
    @FXML
    private File allMusicInPlaylist;
    @FXML
    private File imageDirectory;
    private File[] musicFiles;
    private File[] allMusicFiles;

    private File[] imageFiles;
    private ArrayList<String> songs;
    private ArrayList<String> allSong;
    private ArrayList<File> songInPlaylist;
    private ArrayList<File> allSongInPlaylist;
    private ArrayList<File> images;
    private int songNumber;
    private int[] speeds = {75, 100, 125, 150, 175, 500000};
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean playBtnStatus;
    private boolean randomBtnStatus;
    private boolean loopBtnStatus;
    private String sourcePathMusic = "music";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importMusicDirectory();
        try {
            initInfoSong();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        table.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Song rowData = row.getItem();
                    try {
                        mediaPlayer.stop();
                        initMedia(rowData.getId() - 1);
                        mediaPlayer.play();
                        resetVolumeAndSpeed();
                        beginTimer();
                        playBtnStatus = true;
                        playButton.setText("Pause");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return row;
        });
        createPlaylist.setOpacity(0);
        createPlaylist.setDisable(true);
        namePlaylist.setDisable(true);
        namePlaylist.setOpacity(0);
    }

    public void initInfoSong() throws IOException, ParseException {
        importImagesDirectory();
        initSpeedBox();
        initVolumeBar();
        initTableviewSong();
        getSongTitle(songNumber);
        importPlaylist();
        showPlaylistOnComBox();
        try {
            setLogoSong(songNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            initMedia(songNumber);
            addSongToTbView();
        } catch (TikaException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }

    public void importMusicDirectory() {
        songs = new ArrayList<>();
        musicDirectory = new File(sourcePathMusic);
        musicFiles = musicDirectory.listFiles();
        if (musicFiles != null) {
            for (File file : musicFiles) {
                if (file.getPath().startsWith(".mp3", file.getPath().length()-4)){
                    songs.add(file.getPath());
                }
            }
        }
    }
    public void importImagesDirectory() {
        images = new ArrayList<>();
        imageDirectory = new File("image");
        imageFiles = imageDirectory.listFiles();
        if (imageFiles != null) {
            images.addAll(Arrays.asList(imageFiles));
        }
    }

    IPlaylistService service = new PlaylistService();
    IPlaylistRepository repository = new PlaylistRepository();

    public void importPlaylist() {
        playlistList.addAll(service.getPlaylists());
    }

    public void initSpeedBox() {
        speedBox.getItems().clear();
        for (int speed : speeds) {
            speedBox.getItems().add(Integer.toString(speed));
        }
        speedBox.setOnAction(this::changeSpeed);
    }

    public void initVolumeBar() {
        volumeBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeBar.getValue() * 0.01);
            }
        });
    }

    public void initTableviewSong() {
        songList = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.setItems(songList);
        playlistList = FXCollections.observableArrayList();
        namePlaylist.setPromptText("Enter Name Playlist Want Creat");
    }


    public void showAllSong() {
        songList.clear();
        songs.clear();
        allMusicDirectory = new File(sourcePathMusic);
        allMusicFiles = allMusicDirectory.listFiles();
        if (allMusicFiles != null) {
            for (File file : allMusicFiles) {
                songs.add(file.getPath());
            }
        }
        for (int i = 0; i < songs.size(); i++) {
            String songName = GetTitleSong.get(i, songs);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setName(songName);
            newSong.setPath(songs.get(i));
            songList.add(newSong);
        }
    }

    public void addSongToTbView() throws TikaException, IOException, SAXException {
        for (int i = 0; i < songs.size(); i++) {
            String songName = GetTitleSong.get(i, songs);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setName(songName);
            newSong.setPath(songs.get(i));
            songList.add(newSong);
        }
    }

    public void showPlaylistOnComBox() {
        playlistBox.getItems().clear();
        playlistBox.setPromptText("Select PlayList");
        for (Playlist playlist : playlistList) {
            playlistBox.getItems().add(playlist.getName());
        }
    }

    public String getSongTitle(int songNumber) {
        File musicFile = new File(songs.get(songNumber));
        return musicFile.getName();

    }

    public void getSongInfo(int songIndex) {
        String fileLocation = songs.get(songIndex);
        try {
            InputStream input = new FileInputStream(fileLocation);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            songName.setText(metadata.get("title"));
            singerName.setText(metadata.get("xmpDM:artist"));
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }

    public void initMedia(int songIndex) throws TikaException, IOException, SAXException {
        File musicFile = new File(songs.get(songIndex));
        media = new Media(musicFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        getSongInfo(songIndex);
        setLogoSong(songIndex);
    }

    public String getRelativeName(int songNumber) {
        String songNamePath = getSongTitle(songNumber);
        String relativeSongName = songNamePath.substring(0, songNamePath.length() - 4);
        return relativeSongName + ".jpg";
    }

    public void setLogoSong(int songIndex) throws MalformedURLException {
        File file = new File("src/main/resources/vn/tqt/player/music/image/" + getRelativeName(songIndex));
        String localUrl = file.toURI().toURL().toString();
        Image image = new Image(localUrl);
        if (image.isError()) {
            File fileAvailbleImg = new File("src/main/resources/vn/tqt/player/music/image/availableimage.jpg");
            image = new Image(fileAvailbleImg.toURI().toURL().toString());
        }
        logoSong.setImage(image);
    }

    public void playSong() {
        if (playBtnStatus) {
            mediaPlayer.pause();
            cancelTimer();
            playBtnStatus = false;
            playButton.setText("Play");
        } else {
            mediaPlayer.play();
            getSongInfo(songNumber);
            resetVolumeAndSpeed();
            beginTimer();
            playBtnStatus = true;
            playButton.setText("Pause");
        }
    }

    public void resetVolumeAndSpeed() {
        volumeBar.setValue(75);
        speedBox.setValue("100");
    }

    public void nextSong() throws IOException, TikaException, SAXException {
        if (songNumber < songs.size() - 1) {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            }
        } else {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber = 0;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
                System.out.println(songs.size());
                System.out.println(songNumber);
            } else {
                songNumber = 0;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            }
        }
    }

    public void perviousSong() throws IOException, TikaException, SAXException {
        if (songNumber > 0) {
            if (playBtnStatus) {
                playBtnStatus = false;
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
            }
        }
    }

    public void loopSong() {
        if (!loopBtnStatus) {
            loopBtnStatus = true;
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            loopButton.setText("looping");
            if (randomBtnStatus) {
                randomBtnStatus = false;
                randomButton.setText("random");
            }
        } else {
            loopBtnStatus = false;
            loopButton.setText("loop");
        }

    }

    public void randomSong() {
        if (!randomBtnStatus) {
            randomBtnStatus = true;
            randomButton.setText("randoming");
            if (loopBtnStatus) {
                loopBtnStatus = false;
                loopButton.setText("loop");
            }
        } else {
            randomBtnStatus = false;
            randomButton.setText("random");
        }
    }

    public int randomSongNumber() {
        if (randomBtnStatus) {
            int max = songs.size() - 1;
            return (int) Math.floor(Math.random() * (max + 1));
        }
        return -1;
    }

    public void playRandomSong() throws TikaException, IOException, SAXException {
        mediaPlayer.stop();
        songNumber = randomSongNumber();
        initMedia(songNumber);
        mediaPlayer.play();
        getSongInfo(songNumber);
        beginTimer();
    }

    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                running = true;
                Platform.runLater(() -> {
                    double current = mediaPlayer.getCurrentTime().toSeconds();
                    double end = media.getDuration().toSeconds();
                    double percenttime = (current / end) * 100;
                    int second = (int) current % 60;
                    int minute = (int) (current / 60) % 60;
                    String minutes = String.valueOf(minute);
                    String seconds = String.valueOf(second);
                    songTime.setText(minutes + ":" + seconds);
                    songProgressBar.setProgress(current / end);
                    if (current / end == 1) {
                        if (randomBtnStatus) {
                            try {
                                playRandomSong();
                            } catch (TikaException | IOException | SAXException e) {
                                e.printStackTrace();
                            }
                        }
                        cancelTimer();
                        try {
                            nextSong();
                        } catch (IOException | TikaException | SAXException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void cancelTimer() {
        running = false;
        timer.cancel();
    }

    public boolean checkDuplicatePlaylist(String folderPlaylistName) {
        for (Playlist playlist : playlistList) {
            System.out.println(playlist.getName());
            if (playlist.getName().trim().equals(folderPlaylistName.trim())) {
                return false;
            }
        }
        return true;
    }

    public void createPlaylist() {
        createPlaylist.setOpacity(0);
        createPlaylist.setDisable(true);
        namePlaylist.setDisable(true);
        namePlaylist.setOpacity(0);
        newPlaylistbtn.setDisable(false);
        newPlaylistbtn.setOpacity(1);
        String folderPlaylistName = namePlaylist.getText();
        String titleAlert = "Player Information";
        String contentAlert = "Playlist \"" + folderPlaylistName + "\" already exist";
        if (checkDuplicatePlaylist(folderPlaylistName)) {
            Playlist newPlaylist = new Playlist();
            newPlaylist.setName(folderPlaylistName);
            playlistList.add(newPlaylist);
            repository.update(playlistList);
            namePlaylist.setText("");
            int sizePlaylist = playlistList.size();
            playlistBox.getItems().add(playlistList.get(sizePlaylist - 1).getName());
            newPlaylistbtn.setDisable(false);
            newPlaylistbtn.setOpacity(1);
        } else {
            namePlaylist.setText("");
            InitAlertWindow.initAlert(titleAlert, contentAlert);
        }
    }

    public void playThisList() throws IOException, ParseException {
        String playlistName = playlistBox.getValue();
        if (playlistName != null) {
            if (!checkQuantitySongInPlayList(playlistName)) {
                Playlist newPlaylist = repository.getByName(playlistName);
                songs.clear();
                songs.addAll(newPlaylist.getSongs());
                mediaPlayer.pause();
                initInfoSong();
                playBtnStatus = false;
                playButton.setText("Play");
                playlistBox.setValue(playlistName);
            } else {
                String titleAlert = "Player Warring!!";
                String contentAlert = "The playlist is empty, please add music to the playlist to play music!";
                InitAlertWindow.initAlert(titleAlert, contentAlert);
            }
        } else {
            String titleAlert = "Player Alert!!";
            String contentAlert = "No playlists, create playlists";
            InitAlertWindow.initAlert(titleAlert, contentAlert);
        }
    }
    public Playlist getPLayList(String name) {
        for (Playlist playlist : playlistList) {
            if (playlist.getName().equals(name))
                return playlist;
        }
        return null;
    }
    public void addToPlayList() {
        Song selected = table.getSelectionModel().getSelectedItem();
        String songPath = selected.getPath();
        String playlistName = playlistBox.getValue();
        if (!checkSongInPlaylist(playlistName, songPath)) {
        Playlist newPlaylist = getPLayList(playlistName);
            newPlaylist.getSongs().add(songPath);
            repository.update(playlistList);
            String titleAlert = "ADD Song To Playlist";
            String contentAlert = "Add Song \"" + selected.getName() + "\" to Playlist \"" + playlistName + "\"" + "DONE";
            InitAlertWindow.initAlert(titleAlert, contentAlert);
        } else {
            String titleAlert2 = "Player Alerrt!";
            String contentAlert2 = selected.getName() + " already exists in, cannot be added";
            InitAlertWindow.initAlert(titleAlert2, contentAlert2);
        }
    }

    public boolean checkQuantitySongInPlayList(String playlistName) {
        Playlist playlist = getPLayList(playlistName);
        return playlist.getSongs().isEmpty();
    }

    public boolean checkSongInPlaylist(String playlistName, String pathSong) {
        Playlist newPlaylist = getPLayList(playlistName);
        for (int i = 0; i < newPlaylist.getSongs().size(); i++) {
            if (newPlaylist.getSongs().get(i).equals(pathSong)) {
                return true;
            }
        }
        return false;
    }

    public void deletePlaylist() {
        String playlistName = playlistBox.getValue();
        Playlist newPlaylist = getPLayList(playlistName);
        for (int i = 0; i < service.getPlaylists().size(); i++) {
            if (service.getPlaylists().get(i).equals(newPlaylist)) {
                playlistList.remove(i);
            }
        }
        showPlaylistOnComBox();
        repository.update(playlistList);
    }

    public void chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) pane.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        sourcePathMusic = selectedDirectory.getPath();
        mediaPlayer.stop();
        initialize(null, null);
        playBtnStatus = false;
        playButton.setText("Play");
    }

    public void newPlaylist(ActionEvent actionEvent) {
        createPlaylist.setOpacity(1);
        createPlaylist.setDisable(false);
        namePlaylist.setDisable(false);
        namePlaylist.setOpacity(1);
        newPlaylistbtn.setDisable(true);
        newPlaylistbtn.setOpacity(0);
    }
}
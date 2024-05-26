package com.example.musicservice.Service;

import com.example.musicservice.Data.MusicalTrack;
import com.example.musicservice.Data.MusicalTrackUpload;
import com.example.musicservice.Mapper.Mapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class MusicalTrackService {
    private final MusicalTrackServiceDB musicalTrackServiceDB;

    public MusicalTrackService(MusicalTrackServiceDB musicalTrackServiceDB) {
        this.musicalTrackServiceDB = musicalTrackServiceDB;
    }


    public ResponseEntity<MusicalTrack> getTrackById(Long id) {
        try {
            MusicalTrack out = musicalTrackServiceDB.getMusicalTrackById(id);
            return ResponseEntity.ok(out);
        } catch (NullPointerException e){
            System.out.println("(getTrackById)Перехват исключения" + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            System.out.println("IEE в getTrackById " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    public ResponseEntity<ArrayList<MusicalTrack>> getTrackByName(String name, int page, int size) {
        try {
            if(name.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            ArrayList<MusicalTrack> out = musicalTrackServiceDB.getMusicalTrackByName(name, page, size);
            return ResponseEntity.ok(out);
        } catch (NullPointerException e){
            System.out.println("(getTrackByName) Перехват исключения " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<ArrayList<MusicalTrack>>> getMusicalTrackByGenre(String genre, int page, int size) {
        try {
            CompletableFuture<ArrayList<MusicalTrack>> track = musicalTrackServiceDB.getMusicalTrackByGenre(genre, page, size);
            ArrayList<MusicalTrack> out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getMusicalTrackByGenre) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @Async
    public CompletableFuture<ResponseEntity<ArrayList<MusicalTrack>>> getMusicalTrackByNameOrderByGenre(String name, String genre, int page, int size) {
        try {
            CompletableFuture<ArrayList<MusicalTrack>> track = musicalTrackServiceDB.getMusicalTrackByNameOrderByGenre(name, genre, page, size);
            ArrayList<MusicalTrack> out = track.get();
            return CompletableFuture.completedFuture(ResponseEntity.ok(out));
        } catch (ExecutionException e){
            System.out.println("(getMusicalTrackByNameOrderByGenre) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }


    private CompletableFuture<String> saveFile(MultipartFile file, UUID uuid, String name) {
        String destPath;
        File filePath;
        try {
            if (Objects.equals(file.getContentType(), "audio/mpeg")) {
                destPath = "src/main/resources/static/MusicalTracks";
                filePath = new File(destPath);
                name = name + ".mp3";
            } else if (Objects.equals(file.getContentType(), "text/plain")) {
                destPath = "src/main/resources/static/Lyrics";
                filePath = new File(destPath);
                name = name + ".txt";
            } else if (Objects.equals(file.getContentType(), "image/jpeg")) {
                destPath = "src/main/resources/static/Images/CoverImages";
                filePath = new File(destPath);
                name = name + ".jpg";
            } else {
                throw new IllegalArgumentException("Incorrect file type");
            }
            File dest = new File(filePath.getAbsolutePath(), uuid.toString() + "-" + name);
            file.transferTo(dest);
            return CompletableFuture.completedFuture(destPath + "/" + uuid.toString() + "-" + name);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Incorrect file type: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O error: " + e.getMessage());
        }
    }
    @Async
    public CompletableFuture<ResponseEntity<?>> saveNewTrack(MultipartFile file, MultipartFile lyricsFile, MultipartFile imageFile, MusicalTrackUpload trackUpload) {
        try {
            BufferedImage img = ImageIO.read(imageFile.getInputStream());
            int width = img.getWidth();
            int height = img.getHeight();
            //if (width != height) {
            //    return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only square images are allowed"));
            //}
            MusicalTrack trackData = Mapper.uploadToData(trackUpload);
            String name = trackData.getName();

            UUID uuid = UUID.randomUUID();
            String pathToFile = saveFile(file, uuid, name).get();
            String pathToLyricsFile = saveFile(lyricsFile, uuid, name).get();
            String pathToImageFile = saveFile(imageFile, uuid, name).get();

            trackData.setPathToFile(pathToFile);
            trackData.setPathToLyrics(pathToLyricsFile);
            trackData.setPathToImage(pathToImageFile);

            trackData.setAuditions(0L);
            trackData.setReleaseDate(LocalDateTime.now());

            musicalTrackServiceDB.saveMusicalTrack(trackData);
            return CompletableFuture.completedFuture(ResponseEntity.ok().build());

        } catch (ExecutionException e) {
            System.out.println("(saveTrack or saveFile(?)) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (InterruptedException e) {
            System.out.println("IEE " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (RuntimeException e) {
            System.out.println("(saveFile) Перехват исключения " + e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.internalServerError().build());
        } catch (IOException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Error reading image file"));
        }

    }

    //TODO-add deleteTrack & updateTrack
    //Реализации deleteTrack и updateTrack так же будут,но позже.
    //Обязательно сделать проверку на то, чтобы у удаляющего было право на удаление этого трека
    //(то есть, это должен быть исполнитель, добавивший трек).


    public ResponseEntity<Resource> getTrackFileById(Long id){
        try {
            MusicalTrack track = musicalTrackServiceDB.getMusicalTrackById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }


            String filePath  = Paths
                    .get(track.getPathToFile())
                    .toAbsolutePath()
                    .toString();

            byte[] data = Files.readAllBytes(Paths.get(filePath));
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            System.out.println("IOE (TrackFileById)" + e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            System.out.println("IEE (TrackFileById)" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    public ResponseEntity<Resource> getImageById(Long id){
        try {
            MusicalTrack track = musicalTrackServiceDB.getMusicalTrackById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            String imagePath  = Paths
                    .get(track.getPathToImage())
                    .toAbsolutePath()
                    .toString();

            byte[] imageData = Files.readAllBytes(Paths.get(imagePath));
            ByteArrayResource imageResource = new ByteArrayResource(imageData);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Assuming it's a JPEG image
                    .body(imageResource);

        } catch (IOException e) {
            System.out.println("IOE (ImageById)" + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            System.out.println("IEE (ImageById)" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Resource> getLyricsFileById(Long id){
        try {
            MusicalTrack track = musicalTrackServiceDB.getMusicalTrackById(id);
            if (track == null) {
                return ResponseEntity.notFound().build();
            }

            String filePath  = Paths
                    .get(track.getPathToFile())
                    .toAbsolutePath()
                    .toString();

            byte[] data = Files.readAllBytes(Paths.get(filePath));
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);

        } catch (IOException e) {
            System.out.println("IOE (LyricsFileById)" + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            System.out.println("IEE (LyricsFileById)" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
package com.example.musicservice.Controller;

import com.example.musicservice.Data.MusicalTrack;
import com.example.musicservice.Data.MusicalTrackUpload;
import com.example.musicservice.Service.MusicalTrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/musicalServiceApi")
public class MusicalServiceController {

    private final MusicalTrackService musicalTrackService;

    public MusicalServiceController(MusicalTrackService musicalTrackService) {
        this.musicalTrackService = musicalTrackService;
    }

    @Async
    @GetMapping("/track/{id}")
    public CompletableFuture<ResponseEntity<MusicalTrack>> getTrackByIdController(@PathVariable Long id) {
        return musicalTrackService.getTrackById(id);
    }
    @Async
    @PostMapping("/uploadTrack")
    public CompletableFuture<ResponseEntity<?>> uploadTrack(@RequestParam("trackFile") MultipartFile trackFile,
                                                            @RequestParam("lyricsFile") MultipartFile lyricsFile,
                                                            @RequestParam("imageFile") MultipartFile imageFile,
                                                            @RequestParam("track")MusicalTrackUpload track) {
        if (!Objects.equals(trackFile.getContentType(), "audio/mp3")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only mp3 files are allowed"));
        }
        if (!Objects.equals(lyricsFile.getContentType(), "text/txt")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only txt files are allowed"));
        }
        if (!Objects.equals(imageFile.getContentType(), "image/jpg")) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Only jpg files are allowed"));
        }
        return musicalTrackService.saveNewTrack(trackFile,lyricsFile,imageFile, track);
    }

    @GetMapping("/test")
    public String testController() {
        return "Test endpoint accessed successfully";
    }
}

package com.example.musicservice.Repository;

import com.example.musicservice.Entity.MusicalTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
public interface MusicalTrackRepository extends JpaRepository<MusicalTrackEntity,Long> {

    @Async
    CompletableFuture<MusicalTrackEntity> findByGenre(String genre);//"Find one" - method
    @Async
    CompletableFuture<MusicalTrackEntity> findByName(String name);//"Find one" - method

    @Async
    CompletableFuture<Page<MusicalTrackEntity>> findByName(String name, Pageable pageable);//"Find in range" - method
    @Async
    public CompletableFuture<Page<MusicalTrackEntity>> findByGenre(String name, Pageable pageable);//"Find in range" - method
    @Async
    public CompletableFuture<Page<MusicalTrackEntity>> findByNameOrderByGenre(String name, String genre, Pageable pageable);//"Find in range" - method
}

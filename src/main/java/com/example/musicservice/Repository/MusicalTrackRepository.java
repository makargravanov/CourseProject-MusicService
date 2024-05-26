package com.example.musicservice.Repository;

import com.example.musicservice.Entity.MusicalTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    @Query("SELECT e FROM MusicalTrackEntity e WHERE e.name LIKE %:name%")
    Page<MusicalTrackEntity> findByNameContaining(@Param("name") String name, Pageable pageable);//"Find in range" - method
    @Async
    public CompletableFuture<Page<MusicalTrackEntity>> findByGenre(String name, Pageable pageable);//"Find in range" - method
    @Async
    public CompletableFuture<Page<MusicalTrackEntity>> findByNameOrderByGenre(String name, String genre, Pageable pageable);//"Find in range" - method
}

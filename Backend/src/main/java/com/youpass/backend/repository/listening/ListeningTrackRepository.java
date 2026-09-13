package com.youpass.backend.repository.listening;

import com.youpass.backend.entity.ListeningTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningTrackRepository extends JpaRepository<ListeningTrack, Long> {

}

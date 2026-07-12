package com.samdasu.dodoong.domain.quest.repository;

import com.samdasu.dodoong.domain.quest.entity.DailyQuest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyQuestRepository extends JpaRepository<DailyQuest, Long> {
}

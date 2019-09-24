package com.jojoldu.seminar.springbatch.jobparameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@NoArgsConstructor
public class CreateDateJobParameter {

    @Getter private LocalDate createDate;

    @Value("#{jobParameters[createDate]}")
    public void setCreateDate(String createDate) {
        this.createDate = LocalDate.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

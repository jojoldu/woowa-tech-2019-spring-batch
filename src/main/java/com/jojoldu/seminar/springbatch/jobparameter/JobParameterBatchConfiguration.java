package com.jojoldu.seminar.springbatch.jobparameter;

import com.jojoldu.seminar.springbatch.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class JobParameterBatchConfiguration {
    public static final String BATCH_NAME = "jobParameterBatch";
    public static final String JOB_NAME = BATCH_NAME +"_job";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final CreateDateJobParameter jobParameter;

    @Value("${chunkSize:1000}")
    private int chunkSize;

    @Bean(name = JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean(BATCH_NAME + "jobParameter")
    @JobScope
    public CreateDateJobParameter jobParameter() {
        return new CreateDateJobParameter();
    }

    @Bean(name = BATCH_NAME +"_step")
    public Step step() {
        return stepBuilderFactory.get(BATCH_NAME +"_step")
                .<Product, Product>chunk(chunkSize)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean(name = BATCH_NAME +"_reader")
    @StepScope
    public JpaPagingItemReader<Product> reader() {
        Map<String, Object> params = new HashMap<>();
        params.put("createDate", jobParameter.getCreateDate());

        return new JpaPagingItemReaderBuilder<Product>()
                .name(BATCH_NAME +"_reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Product p WHERE p.createDate =:createDate")
                .parameterValues(params)
                .build();
    }

    private ItemWriter<Product> writer() {
        return items -> {
            for (Product product: items) {
                log.info("Current Product id={}", product.getId());
            }
        };
    }
}

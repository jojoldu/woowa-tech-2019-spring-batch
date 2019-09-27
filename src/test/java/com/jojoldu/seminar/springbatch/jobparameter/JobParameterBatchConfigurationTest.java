package com.jojoldu.seminar.springbatch.jobparameter;

import com.jojoldu.seminar.springbatch.JobTestUtils;
import com.jojoldu.seminar.springbatch.entity.Product;
import com.jojoldu.seminar.springbatch.entity.ProductRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static com.jojoldu.seminar.springbatch.jobparameter.JobParameterBatchConfiguration.JOB_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobParameterBatchConfigurationTest {
    @Autowired private JobTestUtils jobTestUtils;
    @Autowired private ProductRepository productRepository;

    @Test
    public void jobParameter정상출력_확인() throws Exception{
        //given
        LocalDate createDate = LocalDate.of(2019,9,26);
        long price = 1000L;
        productRepository.save(Product.builder().price(price).createDate(createDate).build());

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("createDate", createDate.toString())
                .toJobParameters();
        //when
        JobExecution jobExecution = jobTestUtils.getJobTester(JOB_NAME).launchJob(jobParameters);

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @After
    public void tearDown() throws Exception { productRepository.deleteAll(); }
}

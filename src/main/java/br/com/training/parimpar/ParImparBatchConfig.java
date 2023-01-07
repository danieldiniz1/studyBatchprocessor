package br.com.training.parimpar;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class ParImparBatchConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job imprimeParImparJob(){
        return  jobBuilderFactory.get("imprimeParImparJob").start(imprimeParImparStep())
                .incrementer(new RunIdIncrementer()).build();
    }

    public Step imprimeParImparStep() {
        //implementação do step baseado em chunck

        return stepBuilderFactory
                .get("imprimeParImparStep")
                .<Integer,String>chunk(1)
                .reader(contaAteDezReader())
                .processor(parOuImparProcessor())
                .writer(ImrimeWriter())
                .build();
    }

    public IteratorItemReader<Integer> contaAteDezReader() {
        List<Integer> numbersList = geradorDeNumeros(10);
        return new IteratorItemReader<>(numbersList.iterator());
    }

    public FunctionItemProcessor<Integer,String> parOuImparProcessor(){
        return new FunctionItemProcessor<Integer,String>
        (item -> item % 2 == 0 ? String.format("item %s é Par",item) : String.format("item %s é Imprar",item));
    }

    public ItemWriter<String> ImrimeWriter(){
        return itens -> itens.forEach(i -> LOGGER.info(i.toString()));
    }

    private List<Integer> geradorDeNumeros(int i) {
        Random random = new Random();
        List<Integer> numeros = new ArrayList<>();
        for (int a = 0; a < i; a++) {
            numeros.add(random.nextInt(100));
        }
        return numeros;
    }
}

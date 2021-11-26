package io.spring.batch.javagradle.example.file.common.tokenizer;

import lombok.Setter;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;

public class CustomFileLineTokenizer implements LineTokenizer {

    @Setter
    private String delimiter = ",";

    private String[] names = new String[]{
              "firstName"
            , "middleInitial"
            , "lastName"
            , "address"
            , "city"
            , "state"
            , "zipCode"
    };

    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();

    @Override
    public FieldSet tokenize(String line) {

        // 구분자로 필드 구분
        String[] fields = line.split(delimiter);

        List<String> parsedFields = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (i == 4) {
                // 3,4번쨰 필드 단일 필드로 구성
                parsedFields.set(i - 1, parsedFields.get(i - 1) + " " + fields[i]);
            } else {
                parsedFields.add(fields[i]);
            }
        }


        // 값의 배열 & 필드 이름 배열을 넘겨 필드를 생성
        return fieldSetFactory.create(parsedFields.toArray(new String[0]), names);
    }
}

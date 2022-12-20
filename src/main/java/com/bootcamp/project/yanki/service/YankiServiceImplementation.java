package com.bootcamp.project.yanki.service;

import com.bootcamp.project.yanki.entity.YankiDTO;
import com.bootcamp.project.yanki.entity.YankiEntity;
import com.bootcamp.project.yanki.exception.CustomNotFoundException;
import com.bootcamp.project.yanki.repository.YankiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class YankiServiceImplementation implements YankiService {

    public static final String topic = "mytopic";

    @Autowired
    private KafkaTemplate<String, YankiDTO> kafkaTemp;

    @Autowired
    private YankiRepository yankiRepository;

    @Override
    public Flux<YankiEntity> getAll() {
        return yankiRepository.findAll().switchIfEmpty(Mono.error(new CustomNotFoundException("Yankis not found")));
    }
    @Override
    public Mono<YankiEntity> getOne(String documentNumber) {
        return yankiRepository.findAll().filter(x -> x.getDocumentNumber() != null && x.getDocumentNumber().equals(documentNumber)).next();
    }

    @Override
    public Mono<YankiEntity> save(YankiEntity colEnt) {
        colEnt.setCreateDate(new Date());
        return yankiRepository.save(colEnt);
    }

    @Override
    public Mono<YankiEntity> update(String documentNumber, String email) {
        return getOne(documentNumber).flatMap(c -> {
            c.setEmail(email);
            c.setModifyDate(new Date());
            return yankiRepository.save(c);
        }).switchIfEmpty(Mono.error(new CustomNotFoundException("Yanki not found")));
    }

    @Override
    public Mono<Void> delete(String documentNumber) {
        return getOne(documentNumber)
                .switchIfEmpty(Mono.error(new CustomNotFoundException("Yanki not found")))
                .flatMap(c -> {
                    return yankiRepository.delete(c);
                });
    }
    public Mono<YankiEntity> register(YankiEntity colEnt)
    {
        if(colEnt.getDocumentNumber() == null || colEnt.getMobileNumber() == null || colEnt.getImei() == null || colEnt.getEmail() == null)
        {
            return Mono.error(new CustomNotFoundException("The following fields are mandatory: Document Number, Mobile Number, IMEI and Email."));
        }
        colEnt.setCreateDate(new Date());
        return getOne(colEnt.getDocumentNumber())
                .switchIfEmpty(yankiRepository.save(colEnt));
    }
    @Override
    public Mono<Boolean> checkDebitCardMainAccount(String debitCardNumber)
    {
        return yankiRepository.findAll().filter(x -> x.getDebitCardNumber() != null && x.getDebitCardNumber().equals(debitCardNumber)).hasElements();
    }
    @Override
    public Mono<YankiEntity> linkDebitCard(String documentNumber, String debitCardNumber)
    {
        return getOne(documentNumber).flatMap(c -> checkDebitCardMainAccount(debitCardNumber).flatMap(x -> {
            if (x) {
                return Mono.error(new CustomNotFoundException("The Debit Card Number has already a Yanki account associated."));
            } else {
                c.setDebitCardNumber(debitCardNumber);
                c.setModifyDate(new Date());
                return yankiRepository.save(c);
            }
        })).switchIfEmpty(Mono.error(new CustomNotFoundException("The client does not have a Yanki account")));
    }
    @Override
    public void publishToTopic(String debitCardNumber, String type, Double amount) {
        YankiDTO yankiDTO = new YankiDTO();
        yankiDTO.setDebitCardNumber(debitCardNumber);
        yankiDTO.setType(type);
        yankiDTO.setAmount(amount);
        this.kafkaTemp.send(topic, yankiDTO);
    }
}

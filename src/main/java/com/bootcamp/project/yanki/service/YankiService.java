package com.bootcamp.project.yanki.service;

import com.bootcamp.project.yanki.entity.YankiEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface YankiService {
    public Flux<YankiEntity> getAll();
    public Mono<YankiEntity> getOne(String documentNumber);
    public Mono<YankiEntity> save(YankiEntity colEnt);
    public Mono<YankiEntity> update(String documentNumber, String email);
    public Mono<Void> delete(String documentNumber);
    public Mono<YankiEntity> register(YankiEntity colEnt);
    public Mono<Boolean> checkDebitCardMainAccount(String debitCardNumber);
    public Mono<YankiEntity> linkDebitCard(String documentNumber, String debitCardNumber);
    public void publishToTopic(String debitCardNumber, String type, Double amount);
}

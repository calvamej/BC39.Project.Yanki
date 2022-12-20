package com.bootcamp.project.yanki.controller;

import com.bootcamp.project.yanki.entity.YankiEntity;
import com.bootcamp.project.yanki.service.YankiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/Yanki")
public class YankiController {
    @Autowired
    YankiService clientService;

    @GetMapping(value = "/FindOne/{documentNumber}")
    public Mono<YankiEntity> Get_One(@PathVariable("documentNumber") String documentNumber){
        return clientService.getOne(documentNumber);
    }
    @GetMapping(value = "/FindAll")
    public Flux<YankiEntity> Get_All(){

        return clientService.getAll();
    }
    @PostMapping(value = "/Save")
    public Mono<YankiEntity> Save(@RequestBody YankiEntity col){

        return clientService.save(col);
    }
    @PutMapping(value = "/Update/{documentNumber}/{email}")
    public Mono<YankiEntity> Update(@PathVariable("documentNumber") String documentNumber,@PathVariable("email") String email){
        return clientService.update(documentNumber, email);
    }
    @DeleteMapping  (value = "/Delete/{documentNumber}")
    public Mono<Void> Delete(@PathVariable("documentNumber") String documentNumber){
        return clientService.delete(documentNumber);
    }
    @PostMapping(value = "/Register")
    public Mono<YankiEntity> register(@RequestBody YankiEntity col){

        return clientService.register(col);
    }
    @PutMapping(value = "/LinkDebitCard/{documentNumber}/{debitCardNumber}")
    public Mono<YankiEntity> linkDebitCard(@PathVariable("documentNumber") String documentNumber,@PathVariable("debitCardNumber") String debitCardNumber){
        return clientService.linkDebitCard(documentNumber,debitCardNumber);
    }
    @PostMapping(value="/Post")
    public void sendMessage(@RequestParam("debitCardNumber") String debitCardNumber,@RequestParam("type") String type,@RequestParam("amount") Double amount  ) {
        clientService.publishToTopic(debitCardNumber,type,amount);
    }
}

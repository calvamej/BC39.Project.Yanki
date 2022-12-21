package com.bootcamp.project.yanki.controller;


import com.bootcamp.project.yanki.entity.YankiEntity;
import com.bootcamp.project.yanki.service.YankiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebFluxTest(YankiController.class)
public class YankiControllerImplementationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private YankiService yankiService;

    @Test
    public void save()
    {
        YankiEntity OE = new YankiEntity();
        Mono<YankiEntity> MTest = Mono.just(OE);
        when(yankiService.save(OE)).thenReturn(MTest);
        webTestClient.post().uri("/Yanki/Save")
                .body(Mono.just(MTest),YankiEntity.class)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void update()
    {
        YankiEntity OE = new YankiEntity();
        Mono<YankiEntity> MTest = Mono.just(OE);
        when(yankiService.update("ABC","TEST@ABC.COM")).thenReturn(MTest);
        webTestClient.put().uri("/Yanki/Update/ABC/ABC@ABC.COM")
                .body(Mono.just(MTest),YankiEntity.class)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void delete()
    {
        given(yankiService.delete(any())).willReturn(Mono.empty());
        webTestClient.delete().uri("/Yanki/Delete/ABC")
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    public void getOne()
    {
        YankiEntity OE = new YankiEntity(null,"AAA",null,null,null,null,null,null,null,null,null);
        Mono<YankiEntity> MTest = Mono.just(OE);
        when(yankiService.getOne(any())).thenReturn(MTest);
        Flux<YankiEntity> responseBody = webTestClient.get().uri("/Yanki/FindOne/AAA")
                .exchange()
                .expectStatus().isOk()
                .returnResult(YankiEntity.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getDocumentNumber().equals("AAA"))
                .verifyComplete();
    }
    @Test
    public void getAll()
    {
        YankiEntity OE = new YankiEntity(null,"AAA",null,null,null,null,null,null,null,null,null);
        YankiEntity OE2 = new YankiEntity(null,"BBB",null,null,null,null,null,null,null,null,null);
        Flux<YankiEntity> MTest = Flux.just(OE,OE2);
        when(yankiService.getAll()).thenReturn(MTest);
        Flux<YankiEntity> responseBody = webTestClient.get().uri("/Yanki/FindAll")
                .exchange()
                .expectStatus().isOk()
                .returnResult(YankiEntity.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(OE)
                .expectNext(OE2)
                .verifyComplete();
    }
}

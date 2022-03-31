package co.com.bancolombia.model.postmodel.gateways;

import co.com.bancolombia.model.postmodel.PostModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostModelRepository {

    Flux<PostModel> getAll();
    Mono<PostModel> update(PostModel postModelUpdate);
    Mono<Void> delete(Integer idPostModel);
    Mono<PostModel> create(PostModel postModel);
    Flux<PostModel>findByTitle(String title);
}

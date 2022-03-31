package co.com.bancolombia.usecase.post;

import co.com.bancolombia.model.postmodel.PostModel;
import co.com.bancolombia.model.postmodel.gateways.PostModelRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@AllArgsConstructor
public class PostUseCase {

    private PostModelRepository postModelRepository;

    public Flux<PostModel> getAll(){return postModelRepository.getAll();}
    public Mono<PostModel> update(PostModel postModelUpdate){
        return postModelRepository.update(postModelUpdate);
    }
    public Mono<Void> delete(Integer idPostModel){
        return postModelRepository.delete(idPostModel);
    }
    public Mono<PostModel> create(PostModel postModel){
        return postModelRepository.create(postModel);
    }
    public Flux<PostModel>findByTitle(String title){
        return postModelRepository.findByTitle(title);
    }
}

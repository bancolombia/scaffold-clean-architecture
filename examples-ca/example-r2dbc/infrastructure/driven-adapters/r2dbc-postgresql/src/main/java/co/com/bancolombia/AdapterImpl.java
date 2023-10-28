package co.com.bancolombia;

import co.com.bancolombia.model.postmodel.PostModel;
import co.com.bancolombia.model.postmodel.gateways.PostModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdapterImpl implements PostModelRepository {

    private final MyReactiveRepository myReactiveRepository;

    @Override
    public Flux<PostModel> getAll() {
        return myReactiveRepository.findAll().map(e ->
                PostModel.builder()
                        .id(e.getId())
                        .content(e.getContent())
                        .title(e.getTitle())
                        .build());
    }

    @Override
    public Mono<PostModel> update(PostModel postModelUpdate) {
        return myReactiveRepository
                .findById(postModelUpdate.getId())
                    .flatMap(e -> myReactiveRepository.save(Post.builder()
                            .id(e.getId())
                            .content(postModelUpdate.getContent())
                            .title(postModelUpdate.getTitle())
                            .build())).map(m ->
                        PostModel.builder()
                                .id(m.getId())
                                .content(m.getContent())
                                .title(m.getTitle())
                                .build());
    }

    @Override
    public Mono<Void> delete(Integer idPostModel) {
        return myReactiveRepository.deleteById(idPostModel);
    }

    @Override
    public Mono<PostModel> create(PostModel postModel) {
        return myReactiveRepository.save(Post.builder()
                        .id(postModel.getId())
                        .content(postModel.getContent())
                        .title(postModel.getTitle())
                        .build()).map(m ->
                        PostModel.builder()
                                .id(m.getId())
                                .content(m.getContent())
                                .title(m.getTitle())
                                .build());
    }

    @Override
    public Flux<PostModel> findByTitle(String title) {
        return myReactiveRepository.findByTitle(title).map(e ->
                PostModel.builder()
                        .id(e.getId())
                        .content(e.getContent())
                        .title(e.getTitle())
                        .build());

    }
}

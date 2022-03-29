package co.com.bancolombia.api;
import co.com.bancolombia.model.postmodel.PostModel;
import co.com.bancolombia.usecase.post.PostUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {
    private final PostUseCase postUseCase;


    @GetMapping(path = "/getAll")
    public Flux<PostModel> commandName() {
    return postUseCase.getAll();
    }


    @PostMapping(path ="/create")
    public Mono<PostModel> create(@RequestBody PostModel postModel) {
        return this.postUseCase.create(postModel);
    }

    @GetMapping("/title/{title}")
    public Flux<PostModel> update(@PathVariable("title") String title) {
        return this.postUseCase.findByTitle(title);
    }

    @DeleteMapping("delete/{id}")
    public Mono<Void> delete(@PathVariable("id") Integer id) {
        return this.postUseCase.delete(id);
    }
}

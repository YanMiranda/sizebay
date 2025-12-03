package yan.api.sizebay.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import yan.api.sizebay.repository.ClienteJdbcRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarmupService {

    private final ClienteJdbcRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("Iniciando Warmup");

        for (int i = 0; i < 1000; i++) {
            try {
                repository.findSaldoCliente(1 + (i % 5));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Warmup finalizado!");
    }
}
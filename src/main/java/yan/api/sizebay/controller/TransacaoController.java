package yan.api.sizebay.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yan.api.sizebay.dto.extrato.ExtratoResponse;
import yan.api.sizebay.dto.transacao.TransacaoRequest;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.service.TransacaoService;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService service;

    @PostMapping("/{id}/transacoes")
    public ResponseEntity<TransacaoResponse> createTransaction(
            @PathVariable Integer id,
            @RequestBody @Valid TransacaoRequest request
    ) {
        TransacaoResponse response = service.processTransaction(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/extrato")
    public ResponseEntity<ExtratoResponse> getStatement(@PathVariable Integer id) {
        ExtratoResponse response = service.getStatement(id);
        return ResponseEntity.ok(response);
    }
}

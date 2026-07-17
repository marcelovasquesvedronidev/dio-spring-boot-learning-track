package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionId;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import dio.budgeting.infrastructure.persistence.entity.UserEntity;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input) {
        String userId = usuarioAutenticadoId();

        // CORREÇÃO: Agora usa o novo construtor passando o userId diretamente
        var transaction = transactionRepository.save(
                new Transaction(input.description(), input.amount(), input.category(), userId)
        );

        return TransactionOutput.from(transaction);
    }

    private String usuarioAutenticadoId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // CORREÇÃO: Cast explícito clássico para evitar problemas de compatibilidade/escopo na IDE
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            throw new IllegalStateException("Usuário não está autenticado ou sessão expirou.");
        }

        UserEntity usuario = (UserEntity) authentication.getPrincipal();
        return usuario.getId();
    }
}

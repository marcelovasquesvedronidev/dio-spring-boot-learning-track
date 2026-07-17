package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import dio.budgeting.infrastructure.persistence.entity.UserEntity;

import java.util.List;

@Service
public class ListTransactionsByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionsByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "list-transactions-by-category", description = "Lista transações financeiras por categoria")
    public List<TransactionOutput> execute(
            @ToolParam(description = "Categoria de uma transação") Category category
    ) {
        // CORREÇÃO: Chama o método que já existe no seu repositório da imagem
        return transactionRepository.findAllByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }

    private String usuarioAutenticadoId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity usuario)) {
            throw new IllegalStateException("Usuário não está autenticado ou sessão expirou.");
        }

        return usuario.getId();
    }
}

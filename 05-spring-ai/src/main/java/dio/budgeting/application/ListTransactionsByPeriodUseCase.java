package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListTransactionsByPeriodUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionsByPeriodUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "list-transactions-by-period", description = "Lista transações financeiras entre duas datas")
    public List<TransactionOutput> execute(
            @ToolParam(description = "Data inicial do período (yyyy-MM-dd)") LocalDate start,
            @ToolParam(description = "Data final do período (yyyy-MM-dd)") LocalDate end
    ) {
        return transactionRepository.findByPeriod(start, end)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
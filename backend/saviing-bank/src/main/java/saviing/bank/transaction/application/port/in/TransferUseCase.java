package saviing.bank.transaction.application.port.in;

import saviing.bank.transaction.application.port.in.command.TransferCommand;
import saviing.bank.transaction.application.port.in.result.TransferResult;

/**
 * 송금 유즈케이스에 대한 애플리케이션 계층 포트.
 * 외부 어댑터는 이 인터페이스를 통해 송금 명령을 전달하고 결과를 수신한다.
 */
public interface TransferUseCase {

    /**
     * 송금 명령을 처리하고 결과를 반환한다.
     *
     * @param command 송금 요청 정보
     * @return 송금 처리 결과
     */
    TransferResult transfer(TransferCommand command);
}

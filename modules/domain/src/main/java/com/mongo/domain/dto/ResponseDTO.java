package com.mongo.domain.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Objects;

/**
 * DTO de resposta representando um registro de evento do SPB
 *
 * @author MRAC
 *
 */
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object data;

    private List<ErrorDTO> error;

    /**
     * Construtor basico
     */

    public ResponseDTO() {}

    /**
     * Construtor com o set do objeto retornado
     *
     * @param data retorno de id do evento do SPB
     */

    public ResponseDTO(Object data) {
        this.data = data;
    }

    /**
     * Retorna um objeto com data vazio com multiplos erros
     *
     * @param errors lista de erros
     * @return objeto com multipĺos erros setados
     */

    public static ResponseDTO withErrors(List<ErrorDTO> errors) {
        ResponseDTO restReturn = new ResponseDTO();
        restReturn.setError(errors);
        return restReturn;
    }

    /**
     * Retorna um objeto com data vazio com apenas um erro
     *
     * @param error conteudo com um unico erro
     * @return objeto com apenas um erro setados
     */

    public static ResponseDTO withError(ErrorDTO error) {
        return withErrors(Arrays.asList(new ErrorDTO[] {error}));
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<ErrorDTO> getError() {
        return error;
    }

    public void setError(List<ErrorDTO> error) {
        this.error = error;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.data);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ResponseDTO) {
            ResponseDTO that = (ResponseDTO) object;
            return Objects.equal(this.data, that.data);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ResponseDTO [data=" + data + ", error=" + error + "]";
    }
}
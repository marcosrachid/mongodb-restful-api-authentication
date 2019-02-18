package com.mongo.domain.dto;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * DTO de resposta um erro ao adicionar um registro de evento no SPB
 *
 * @author MRAC
 *
 */
public class ErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String message;

    private String field;

    private String action;

    /**
     * Construtor basico
     */
    public ErrorDTO() {}

    /**
     * Construtor de erro com o set de todos os parametros
     *
     * @param code codigo http de erro
     * @param message mensagem de erro especificada
     * @param field campo em que houve o erro
     * @param action acao em que houve o erro
     */
    public ErrorDTO(String code, String message, String field, String action) {
        this.code = code;
        this.message = message;
        this.field = field;
        this.action = action;
    }

    /**
     * Construtor de erro sem a especificação da acao do evento
     *
     * @param code codigo http de erro
     * @param message mensagem de erro especificada
     * @param field campo em que houve o erro
     */
    public ErrorDTO(String code, String message, String field) {
        this.code = code;
        this.message = message;
        this.field = field;
    }

    /**
     * Construtor de erro simples apenas com o código e mensagem de erro
     *
     * @param code codigo http de erro
     * @param message mensagem de erro especificada
     */
    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.code, this.action, this.field, this.message);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ErrorDTO) {
            ErrorDTO that = (ErrorDTO) object;
            return Objects.equal(this.code, that.code) && Objects.equal(this.action, that.action) && Objects.equal(this.field, that.field)
                    && Objects.equal(this.message, that.message);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ErrorDTO [code=" + code + ", message=" + message + ", field=" + field + ", action=" + action + "]";
    }
}
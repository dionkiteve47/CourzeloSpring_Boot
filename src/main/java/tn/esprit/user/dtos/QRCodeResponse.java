package tn.esprit.user.dtos;

import lombok.Data;

@Data
public class QRCodeResponse {
    private String qrCodeImage;

    public QRCodeResponse(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }
}

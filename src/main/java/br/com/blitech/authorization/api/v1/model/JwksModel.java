package br.com.blitech.authorization.api.v1.model;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JwkModel", description = "Details about a JSON Web Key")
public class JwksModel {

    @Schema(example = "RSA")
    private final String kty;

    @Schema(example = "1")
    private final String kid;

    @Schema(example = "sig")
    private final String use;

    @Schema(example = "RS256")
    private final String alg;

    @Schema(example = "base64url-encoded-modulus")
    private final String n;

    @Schema(example = "base64url-encoded-exponent")
    private final String e;

    public JwksModel(String kty, String kid, String alg, String n, String e) {
        this.kty = kty;
        this.kid = kid;
        this.use = "sig";
        this.alg = alg;
        this.n = n;
        this.e = e;
    }

    public String getKty() { return kty; }
    public String getKid() { return kid; }
    public String getUse() { return use; }
    public String getAlg() { return alg; }
    public String getN() { return n; }
    public String getE() { return e; }
}

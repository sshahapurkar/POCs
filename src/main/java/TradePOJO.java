import java.io.Serializable;

public class TradePOJO implements Serializable {
    private int tradeId;
    private String assetClass;
    private String countryOfOrigin;
    private String currency;

    public TradePOJO(int tradeId, String assetClass, String countryOfOrigin, String currency) {
        this.tradeId = tradeId;
        this.assetClass = assetClass;
        this.countryOfOrigin = countryOfOrigin;
        this.currency = currency;
    }

    public TradePOJO() {

    }

    public int getTradeId() {
        return tradeId;
    }

    @Override
    public String toString() {
        return "TradePOJO{" +
                "tradeId=" + tradeId +
                ", assetClass='" + assetClass + '\'' +
                ", countryOfOrigin='" + countryOfOrigin + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }
}

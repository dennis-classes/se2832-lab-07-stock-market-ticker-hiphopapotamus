import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class StockQuoteAnalyzerTest {
    @Mock
    private StockQuoteGeneratorInterface generatorMock;
    @Mock
    private StockTickerAudioInterface audioMock;

    private StockQuoteAnalyzer analyzer;

    @BeforeMethod
    public void setUp() throws Exception {
        generatorMock = mock(StockQuoteGeneratorInterface.class);
        audioMock = mock(StockTickerAudioInterface.class);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        generatorMock = null;
        audioMock = null;
    }

    @Test(expectedExceptions = InvalidStockSymbolException.class)
    public void constructorShouldThrowExceptionWhenSymbolIsInvalid() throws Exception {
        analyzer = new StockQuoteAnalyzer("ZZZZZZZZZ", generatorMock, audioMock);
    }

    @Test
    public void constructorShouldConstructAndSetSymbolWhenSymbolIsValid() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        assertEquals(analyzer.getSymbol(), "AAC");
    }

    @Test
    public void playAppropriateAudioShouldPlayHappyMusicWhenPercentChangeIsGreaterThan0() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        setGeneratorToAddQuote(null, 0, 12,12);

        analyzer.refresh();
        analyzer.playAppropriateAudio();

        verify(audioMock).playHappyMusic();
    }

    @Test
    public void playAppropriateAudioShouldPlaySadMusicWhenPercentChangeIsLessThan0() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        setGeneratorToAddQuote(null, 0, 0.2,-0.4);

        analyzer.refresh();
        analyzer.playAppropriateAudio();

        verify(audioMock).playSadMusic();
    }

    @Test
    public void playAppropriateAudioShouldNotPlayAnyMusicWhenPercentChangeIs0() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        setGeneratorToAddQuote(null, 0, 0,0.2);

        analyzer.refresh();
        analyzer.playAppropriateAudio();
    }

    @Test
    public void playAppropriateAudioShouldPlayErrorMusicWhenChangeIsInvalid() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        analyzer.playAppropriateAudio();

        verify(audioMock).playErrorMusic();
    }

    @Test(expectedExceptions = StockTickerConnectionError.class)
    public void refreshShouldThrowExceptionWhenItCannotObtainAQuote() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        when(generatorMock.getCurrentQuote()).thenThrow(Exception.class);
        analyzer.refresh();
    }

    @Test
    public void refreshShouldNotThrowAnExceptionWhenItCanObtainAQuote() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        setGeneratorToAddQuote("red", 0, 0, 0);
        analyzer.refresh();
    }

    @Test
    public void getSymbolShouldReturnCorrectSymbolWhenCalled() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        assertEquals("AAC", analyzer.getSymbol());
    }

    @Test(expectedExceptions = InvalidAnalysisState.class)
    public void getCurrentPriceShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        analyzer.getCurrentPrice();
    }

    @Test
    public void getCurrentPriceShouldReturnLastTradedValueWhenCurrentQuoteIsNotNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        setGeneratorToAddQuote("red", 100, 0, 0);
        analyzer.refresh();
        assertEquals(analyzer.getCurrentPrice(), 100, .0001);
    }

    @Test(expectedExceptions = InvalidAnalysisState.class)
    public void getPreviousCloseShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        analyzer.getPreviousClose();
    }

    @Test
    public void getPreviousCloseShouldReturnLastClosingValueWhenCurrentQuoteIsNotNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        setGeneratorToAddQuote("red", 0, 10, 0);
        analyzer.refresh();
        assertEquals(analyzer.getPreviousClose(), 10, .0001);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getChangeSinceCloseShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        analyzer.getChangeSinceClose();
    }

    @Test
    public void getChangeSinceCloseShouldReturnChangeWhenCurrentQuoteIsNotNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);
        setGeneratorToAddQuote("red", 0, 10, 20);
        analyzer.refresh();
        assertEquals(analyzer.getChangeSinceClose(), 10, .0001);
    }

    private void setGeneratorToAddQuote(String symbol, double lastTrade, double close, double change) throws Exception {
        when(generatorMock.getCurrentQuote()).thenReturn(new StockQuoteInterface() {
            @Override
            public String getSymbol() {
                return symbol;
            }

            @Override
            public double getLastTrade() {
                return lastTrade;
            }

            @Override
            public double getClose() {
                return close;
            }

            @Override
            public double getChange() {
                return change;
            }
        });
    }
}
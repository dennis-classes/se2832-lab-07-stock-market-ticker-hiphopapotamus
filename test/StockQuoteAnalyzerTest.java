import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;
import org.mockito.Mock;
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

        when(generatorMock.getCurrentQuote()).thenReturn(new StockQuoteInterface() {
            @Override
            public String getSymbol() {
                return null;
            }

            @Override
            public double getLastTrade() {
                return 0;
            }

            @Override
            public double getClose() {
                return 12;
            }

            @Override
            public double getChange() {
                return 12;
            }
        });

        analyzer.refresh();
        analyzer.playAppropriateAudio();

        verify(audioMock).playHappyMusic();
    }

    @Test
    public void playAppropriateAudioShouldPlaySadMusicWhenPercentChangeIsLessThan0() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        when(generatorMock.getCurrentQuote()).thenReturn(new StockQuoteInterface() {
            @Override
            public String getSymbol() {
                return null;
            }

            @Override
            public double getLastTrade() {
                return 0;
            }

            @Override
            public double getClose() {
                return 0.2;
            }

            @Override
            public double getChange() {
                return -0.4;
            }
        });

        analyzer.refresh();
        analyzer.playAppropriateAudio();

        verify(audioMock).playSadMusic();
    }

    @Test
    public void playAppropriateAudioShouldNotPlayAnyMusicWhenPercentChangeIs0() throws Exception {
        analyzer = new StockQuoteAnalyzer("AAC", generatorMock, audioMock);

        when(generatorMock.getCurrentQuote()).thenReturn(new StockQuoteInterface() {
            @Override
            public String getSymbol() {
                return null;
            }

            @Override
            public double getLastTrade() {
                return 0;
            }

            @Override
            public double getClose() {
                return 0.2;
            }

            @Override
            public double getChange() {
                return 0;
            }
        });

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
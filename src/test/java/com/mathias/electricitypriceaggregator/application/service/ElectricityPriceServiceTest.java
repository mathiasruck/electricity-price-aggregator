package com.mathias.electricitypriceaggregator.application.service;

import com.mathias.electricitypriceaggregator.domain.model.ElectricityPrice;
import com.mathias.electricitypriceaggregator.domain.model.ElectricityPriceEstonia;
import com.mathias.electricitypriceaggregator.infrastructure.persistence.repository.ElectricityPriceBulkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElectricityPriceServiceTest {
    @InjectMocks
    private ElectricityPriceService electricityPriceService;

    @Mock
    private ElectricityPriceBulkRepository bulkRepository;

    @Test
    void processCsvUpload_withValidData() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;40,01;40,01;40,01;40,01
                1704063600;01.01.2024 01:00;38,37;38,37;38,37;38,37
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ElectricityPrice>> priceCaptor = ArgumentCaptor.forClass((Class<List<ElectricityPrice>>) (Class<?>) List.class);

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(priceCaptor.capture());
        List<ElectricityPrice> capturedPrices = priceCaptor.getValue();
        assertEquals(2, capturedPrices.size());

        ElectricityPriceEstonia price = (ElectricityPriceEstonia) capturedPrices.get(0);
        assertEquals(40.01, price.getPrice());
        assertEquals(1704060000L, price.getRecordedAt());
        assertEquals("EE", price.getCountry());

        ElectricityPriceEstonia price2 = (ElectricityPriceEstonia) capturedPrices.get(1);
        assertEquals(38.37, price2.getPrice());
        assertEquals(1704063600, price2.getRecordedAt());
        assertEquals("EE", price2.getCountry());
    }

    @Test
    public void processCsvUpload_withEmptyFile() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = "Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti\n";

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(argThat(List::isEmpty));
    }

    @Test
    public void processCsvUpload_withMalformedData() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                invalid;01.01.2024 00:00;40,01;40,01;40,01;40,01
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        Exception exception = assertThrows(Exception.class,
                () -> electricityPriceService.processCsvUpload(file));
        assertNotNull(exception);
        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Failed to read file"));

        assertThrows(RuntimeException.class, () -> electricityPriceService.processCsvUpload(file));
        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withBlankLines() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;40,01;40,01;40,01;40,01
                
                1704063600;01.01.2024 01:00;38,37;38,37;38,37;38,37
                
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(argThat(list -> {
            if (list.size() != 2) {
                return false;
            }
            ElectricityPriceEstonia price1 = (ElectricityPriceEstonia) list.get(0);
            ElectricityPriceEstonia price2 = (ElectricityPriceEstonia) list.get(1);
            return price1.getPrice() == 40.01 && price2.getPrice() == 38.37;
        }));
    }

    @Test
    public void processCsvUpload_withDifferentDecimalFormats() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;40,010;40,000;40,0;40,00
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ElectricityPrice>> priceCaptor = ArgumentCaptor.forClass((Class<List<ElectricityPrice>>) (Class<?>) List.class);

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(priceCaptor.capture());
        List<ElectricityPrice> capturedPrices = priceCaptor.getValue();
        assertEquals(1, capturedPrices.size());

        ElectricityPriceEstonia price = (ElectricityPriceEstonia) capturedPrices.get(0);
        assertEquals(40.00, price.getPrice());
        assertEquals("EE", price.getCountry());
    }

    @Test
    public void processCsvUpload_withNegativeAndZeroPrices() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;40,01;40,01;40,01;-999,99
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ElectricityPrice>> priceCaptor = ArgumentCaptor.forClass((Class<List<ElectricityPrice>>) (Class<?>) List.class);

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(priceCaptor.capture());
        List<ElectricityPrice> capturedPrices = priceCaptor.getValue();
        assertEquals(1, capturedPrices.size());
        ElectricityPriceEstonia price = (ElectricityPriceEstonia) capturedPrices.get(0);
        assertEquals(-999.99, price.getPrice());
        assertEquals("EE", price.getCountry());
    }

    @Test
    public void processCsvUpload_withSpecialCharacters() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;40,01;40,01;40,01;40,01
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(argThat(list -> {
            if (list.isEmpty()) {
                return false;
            }
            ElectricityPriceEstonia price = (ElectricityPriceEstonia) list.get(0);
            return price.getPrice() == 40.01;
        }));
    }

    @Test
    public void processCsvUpload_withMissingColumns() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String invalidHeader = "Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome\n";
        String csvContent = invalidHeader + "1704060000;01.01.2024 00:00;40,01;40,01;40,01";

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        Exception exception = assertThrows(Exception.class,
                () -> electricityPriceService.processCsvUpload(file));
        assertNotNull(exception);
        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withoutHeaders() throws IOException {

        MultipartFile file = mock(MultipartFile.class);
        String csvContent = "1704060000;01.01.2024 00:00;40,01;40,01;40,01;40,01\n";
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));


        Exception exception = assertThrows(RuntimeException.class,
                () -> electricityPriceService.processCsvUpload(file));
        assertNotNull(exception.getMessage());
        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withNoContent() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = "";
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        Exception exception = assertThrows(RuntimeException.class,
                () -> electricityPriceService.processCsvUpload(file));
        assertNotNull(exception.getMessage());
        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withInvalidEncoding() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{(byte) 0xFF, (byte) 0xFE}));

        Exception exception = assertThrows(RuntimeException.class,
                () -> electricityPriceService.processCsvUpload(file));
        assertNotNull(exception.getMessage());

        verify(bulkRepository, never()).upsertAll(any());
    }

    @Test
    public void processCsvUpload_withVeryLargeValues() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = """
                Ajatempel (UTC);Kuupäev (Eesti aeg);NPS Läti;NPS Leedu;NPS Soome;NPS Eesti
                1704060000;01.01.2024 00:00;99999,99;99999,99;99999,99;99999,99
                """;
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.ISO_8859_1)));

        electricityPriceService.processCsvUpload(file);

        verify(bulkRepository).upsertAll(argThat(list -> {
            if (list.isEmpty()) return false;
            ElectricityPriceEstonia price = (ElectricityPriceEstonia) list.get(0);
            return price.getPrice() == 99999.99;
        }));
    }
}

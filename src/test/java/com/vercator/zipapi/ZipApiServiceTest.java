package com.vercator.zipapi;

import com.vercator.zipapi.exception.ZipApiException;
import com.vercator.zipapi.model.ZipFile;
import com.vercator.zipapi.service.ZipApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ZipApiServiceTest {

	@Autowired
	ZipApiService zipApiService;

	@Test
	public void shouldZipFiles() {

		try {
			String requestId = UUID.randomUUID().toString();
			ZipFile zipFile = zipApiService.zipFiles(createMultipartFiles(), requestId);
			assertEquals(requestId, zipFile.getId());
			assertTrue(zipFile.getSizeMB() > 0);
		} catch (ZipApiException e) {
			fail("Should not have thrown any exception");
		} catch (IOException e) {
			fail("Should not have thrown any exception");
		}
	}

	private MultipartFile[] createMultipartFiles() throws IOException {
		File file1 = new File("src/test/resources/file1.txt");
		File file2 = new File("src/test/resources/file2.txt");
		MultipartFile[]  multipartFileArray = new MultipartFile[2];
		MockMultipartFile mfile1 = new MockMultipartFile(file1.getName(), file1.getName(), null, Files.readAllBytes(file1.toPath()));
		multipartFileArray[0] = mfile1;
		MockMultipartFile mfile2 = new MockMultipartFile(file2.getName(), file2.getName(), null, Files.readAllBytes(file2.toPath()));
		multipartFileArray[1] = mfile2;
		return multipartFileArray;
	}


}
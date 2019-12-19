/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.mockito.Mockito;

public class ClientTestUtil {
    public static Client MockClientByFinder(byte[] stream, Client client) throws IOException {
        var mockedClientOnDependency = Mockito.mock(ClientOnDependency.class);

        Mockito.doNothing().when(mockedClientOnDependency).connection(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(Client.DEFAULT_CORRECT_RESPONSE_CODE).when(mockedClientOnDependency).connectionResponseCode();

        if (stream != null) {
            Mockito.doReturn(new ByteArrayInputStream(stream)).when(mockedClientOnDependency).connectionInputStream();
        } else {
            Mockito.doThrow(new IOException()).when(mockedClientOnDependency).connectionInputStream();
        }

        var mockedClient = Mockito.spy(client);

        Mockito.doReturn(mockedClientOnDependency).when(mockedClient).getClientOnDependency();
        return mockedClient;
    }

    public static Client MockClientByApp(Client client) throws IOException {
        var mockedClient = Mockito.spy(client);

        var mockedClientOnDependency = Mockito.mock(ClientOnDependency.class);

        Mockito.doNothing().when(mockedClientOnDependency).connection(Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(Client.DEFAULT_CORRECT_RESPONSE_CODE).when(mockedClientOnDependency).connectionResponseCode();
        Mockito.doReturn(new ByteArrayInputStream(
                "{\"failing\" : 0, \"pending\" : 0, \"inProgress\" : 0}".getBytes(StandardCharsets.UTF_8)))
                .when(mockedClientOnDependency).connectionInputStream();
        Mockito.doReturn(mockedClientOnDependency).when(mockedClient).getClientOnDependency();

        return mockedClient;
    }
}

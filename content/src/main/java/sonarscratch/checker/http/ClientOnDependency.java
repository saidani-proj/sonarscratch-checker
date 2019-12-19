/**
 * sonarscratch.checker project
 * Copyright (c) tcdorg. All rights reserved.
 * Licensed under the MIT License. See LICENSE.txt in the project root for license information.
 */

package sonarscratch.checker.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

class ClientOnDependency {
    private HttpURLConnection connection;

    ClientOnDependency() {
        connection = null;
    }

    void connection(String url, boolean asAdmin) throws IOException {
        connection = Client.createConnection(url, asAdmin);
    }

    int connectionResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    void connectionDisconnect() {
        connection.disconnect();
    }

    InputStream connectionInputStream() throws IOException {
        return connection.getInputStream();
    }
}

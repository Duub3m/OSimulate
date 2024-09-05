public class VFS implements Device {
    private DeviceMapping[] mappings = new DeviceMapping[10];

    public VFS() {
        // Initialize the mappings array with null values
        for (int i = 0; i < mappings.length; i++) {
            mappings[i] = null;
        }
    }

    @Override
    public int Open(String input) {
        // Split the input string to extract the device name and the parameters
        String[] parts = input.split(" ", 2);
        String deviceName = parts[0];
        String parameters = parts.length > 1 ? parts[1] : "";

        // Determine the appropriate device based on the deviceName
        Device device = determineDevice(deviceName);
        if (device == null) {
            throw new IllegalArgumentException("Device not found: " + deviceName);
        }

        int deviceId = device.Open(parameters);
        if (deviceId == -1) {
            return -1; // The device failed to open
        }

        // Store the device and its ID in the mappings array
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i] == null) {
                mappings[i] = new DeviceMapping(device, deviceId);
                return i; // Return the VFS ID
            }
        }

        return -1; // No space in mappings array
    }


    @Override
    //Close: Removes the Device and Id entries
    public void Close(int vfsId) {
        if (isValidVfsId(vfsId)) {
            DeviceMapping mapping = mappings[vfsId];
            mapping.device.Close(mapping.deviceId);
            mappings[vfsId] = null; // Clear the mapping after closing the device
        }
    }

    @Override
    //Read
    // Check if the VFS ID is valid 
    public byte[] Read(int vfsId, int size) {
        if (isValidVfsId(vfsId)) {
            DeviceMapping mapping = mappings[vfsId];
            return mapping.device.Read(mapping.deviceId, size);
        }
        return new byte[0];
    }

    @Override
    //Seek
    public void Seek(int vfsId, int position) {
        // Checks the VFS ID before seeking
        if (isValidVfsId(vfsId)) {
            DeviceMapping mapping = mappings[vfsId];
            mapping.device.Seek(mapping.deviceId, position);
        }
    }

    @Override
    //Write
    public int Write(int vfsId, byte[] data) {
        // Makes sure the VFS ID is correct before we start writing
        if (isValidVfsId(vfsId)) {
            DeviceMapping mapping = mappings[vfsId];
            return mapping.device.Write(mapping.deviceId, data);
        }
        return 0;
    }
    //VFS ID has to be within a valid range, so it cant be null
    private boolean isValidVfsId(int vfsId) {
        return vfsId >= 0 && vfsId < mappings.length && mappings[vfsId] != null;
    }

    // selects the corresponding device based on its name.
    private Device determineDevice(String deviceName) {
        switch (deviceName.toLowerCase()) {
            case "random":
                return new RandomDevice();
            case "file":
                return new FakeFileSystem(); 
            default:
                return null; // No device that matches
        }
    }
}

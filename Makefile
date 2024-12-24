# Makefile for Emergency Supply Network Project

# Compiler and Executable
JAVAC = javac
JAVA = java

# JSON Library
CLASSPATH = .:java-json.jar

# Source Files
SRC = EmergencySupplyNetwork.java ResourceRedistribution.java DynamicResourceSharing.java NetworkApp.java City.java Warehouse.java

# Targets
.PHONY: all clean run

# Default target
all: compile

# Compile all Java files
compile:
	$(JAVAC) -cp $(CLASSPATH) $(SRC)

# Run the program with both test cases and save the outputs
run: compile
	mkdir -p outputs
	$(JAVA) -cp $(CLASSPATH) NetworkApp TestCase1.txt > outputs/TestCase1.json
	$(JAVA) -cp $(CLASSPATH) NetworkApp TestCase2.txt > outputs/TestCase2.json

# Clean compiled files
clean:
	rm -f *.class


#include <stdio.h>
#include <stdint.h>

#define READ		0
#define WRITE 		1
#define BUS_READ 	2
#define BUS_WRITE 	3
#define MODIFIED 	0
#define EXCLUSIVE 	1
#define SHARED 		2
#define INVALID 	3

char Bus;

int main() {
	return 1;
}

char nextMesiState(char state, char action) {
	char nextState;
	switch (state) {
	case (MODIFIED):
		switch (action) {
		case READ:
			return MODIFIED;
		case WRITE:
			return MODIFIED;
		case BUS_READ:
			return SHARED;//Need to flush
		case BUS_WRITE:
			return INVALID;//Need to flush
		}
	case EXCLUSIVE:
		switch (action) {
		case READ:
			return EXCLUSIVE;
		case WRITE:
			return MODIFIED;
		case BUS_READ:
			return SHARED;
		case BUS_WRITE:
			return INVALID;//Need to flush
		}
	case SHARED:
		switch (action) {
		case READ:
			return SHARED;
		case WRITE:
			return MODIFIED;//Need to send BusRd_Ex
		case BUS_READ:
			return SHARED;
		case BUS_WRITE:
			return INVALID;//Need to flush
		}
	case INVALID:
		switch (action) {
		case READ:
			return EXCLUSIVE;
		case WRITE:
			return MODIFIED;
		case BUS_READ:
			return INVALID;
		case BUS_WRITE:
			return INVALID;
		}
	}
}

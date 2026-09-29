import SwiftUI

struct ContentView: View {
    var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea()

            VStack(spacing: 24) {
                Text("Gastro Guard")
                    .font(.system(size: 34, weight: .bold))
                    .foregroundColor(.white)

                Text("Food Safety & Hygiene")
                    .font(.system(size: 18))
                    .foregroundColor(.green)

                Text("iOS version")
                    .foregroundColor(.gray)
            }
        }
    }
}

#Preview {
    ContentView()
}
